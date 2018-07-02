import { Template } from 'meteor/templating';
import { ReactiveVar } from 'meteor/reactive-var';
import { Mongo } from 'meteor/mongo';

Wallets = new Mongo.Collection('Wallets');

import './main.html';

var client = new CoinStack('c7dbfacbdf1510889b38c01b8440b1',
  '10e88e9904f29c98356fd2d12b26de', 'testchain.blocko.io', 'https'); console.log(client);

Template.body.helpers({
  isDetail() {
    return Session.get('isDetail');
  },
  mypic() {
    return Meteor.user().profile.thumbnail_image;
  }
});

Template.walletlist.onCreated(function helloOnCreated() {
  this.counter = new ReactiveVar(0);
});

Template.walletlist.helpers({
  wallets() {
    return Wallets.find();
  }
});

Template.walletlist.events({
  'click button': function (event, instance) {
    var dbparam = {};

    var label = prompt("Please enter your name", "지갑이름을 입력해주세요.");

    dbparam.label = label;
    dbparam.privkey = CoinStack.ECKey.createKey();
    dbparam._id = CoinStack.ECKey.deriveAddress(dbparam.privkey);

    Meteor.call('createWallet', dbparam, function (error) {
      if (error) alert(error);
    })
  },
  'click div[name=wallet]': function (e, i) {
    var address = $(e.currentTarget).attr('address');
    console.log('clicked');

    Session.set('address', address);
    Session.set('isDetail', true);
  }
});

Template.walletdetail.onRendered(function () {
  $('#qrcode').qrcode({
    size: 150,
    text: 'bitcoin : ' + Session.get('address')
  });
});

Template.walletdetail.helpers({
  'address': function () {
    return Session.get('address');
  }
});
