import { Meteor } from 'meteor/meteor';

Wallets = new Mongo.Collection('Wallets');

Meteor.startup(() => {
  // code to run on server at startup
});

Meteor.methods({
  'createWallet': function (dbparam) {
    if(!dbparam.label){
      throw new Meteor.Error('No Label error');
    }
    dbparam.createAt = new Date();
    console.log(dbparam);
    Wallets.insert(dbparam);
    return true;
  },
});