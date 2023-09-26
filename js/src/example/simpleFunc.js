const testString = (value) => {
  return "got native value:" + value;
};

const test2String = (value, value2) => {
  return "got native value1:" + value + " value2:" + value2;
};

const testObj = (nativeObj) => {
  return "got native obj:" + JSON.stringify(nativeObj);
};

module.exports = {
  test2String, testObj, testString
};


