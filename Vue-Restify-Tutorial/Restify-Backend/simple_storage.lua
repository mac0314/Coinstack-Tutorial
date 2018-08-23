local system = require("system");

function set(key, value) 
  system.print("key: " .. key .. " value: " .. value);    
  system.setItem(key, value)
end 

function get(key)
  local value = system.getItem(key);    
  
  system.print("key: " .. key .. " value: " .. value);    
  
  return value;
end