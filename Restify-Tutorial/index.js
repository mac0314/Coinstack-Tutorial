var restify = require('restify');
var CoinStack = require('coinstack-sdk-js');
var client = new CoinStack('', '', '172.16.101.132:3000', 'http'); 
var fs = require('fs');

var definition = fs.readFileSync('./simple_storage.lua', 'utf8');

var address = "1QBSiKdw52XDDDM17fp4bohcM3NcH8Bne1";
var privatekey = "Ky177r3bogwAxhbyVrBKK44gHxKBFtCsRFN7A3ZqHhWkm6GitYWW";
var fee = 10000;

var builder = client.createLuaContractBuilder();
builder = builder.setInput(address).setContractID(address);
builder.setFee(fee);
builder.setDefinition(definition);
builder.buildTransaction(function (err, tx) {
  if (err) {
    console.log("Failed to create a transaction -", err);
    return;
  }

  try {
    tx.sign(privatekey);
    var rawTx = tx.serialize();
    var hash = tx.getHash();
    client.sendTransaction(rawTx, function (err) {
      if (err) {
        console.log("Failed to broadcast transaction -", hash, "(", err, ")")
        return;
      }
      console.log("Sent transaction -", hash);
    });
  } catch (e) {
    console.log(e);
  }
});

var server = restify.createServer({
  name: "Rocky-restify",
  version: "0.0.1"
});

server.use(restify.plugins.acceptParser(server.acceptable));
server.use(restify.plugins.queryParser());
server.use(restify.plugins.bodyParser());

server.listen(8080, function () {
  console.log('ready on %s', server.url);
});


server.get('/api/message', function get(req, res, next) {
  getMessage(req, res, next);
});

server.post('/api/message', function create(req, res, next) {
  saveMessage(req, res, next);
});

function saveMessage(req, res, next) {
  console.log(req.body);
  var code = 'res, ok = call("set", "message", "' + req.body + '"); assert(ok); return res;';

  //console.log(code);

  var builder = client.createLuaContractBuilder();
  builder = builder.setInput(address).setContractID(address);
  builder.setFee(fee);
  builder.setExecution(code);
  builder.buildTransaction(function (err, tx) {
    if (err) {
      console.log("Failed to create a transaction -", err);
      res.send(500, {msg: "Failed to create a tx"});
      next();
      return;
    }

    try {
      tx.sign(privatekey);
      var rawTx = tx.serialize();
      var hash = tx.getHash();
      client.sendTransaction(rawTx, function (err) {
        if (err) {
          console.log("Failed to broadcast transaction -", hash, "(", err, ")")
          res.send(500, {msg: "Failed to create a tx"});
          next();
          return;
        }
        console.log("Sent transaction -", hash);
        res.send(200, {msg:"Saved message"});
        next();
      });
    } catch (e) {
      console.log(e);
      res.send(500, {msg:"Failed to create a tx"});
      next();
      return;
    }
  });
}

function getMessage(req, response, next) {
  var code = 'res, ok = call("get", "message"); assert(ok); return res;';

  //console.log(code);

  client.queryContract(address, "LSC", code, function (err, res) {
    console.log(err);
    console.log(res);
    response.send(200, res);
    next();
    return;
  });
}