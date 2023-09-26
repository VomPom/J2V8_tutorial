const testPromise = async () => {
  let sum = 0;
  for (let i = 0; i <= 1000000; i++) {
    sum += i;
  }
  return new Promise((resolve, reject) => {
    console.log('From 0 to 100000000 sum=' + sum);
    resolve(sum);
  });
};

const testConsoleLog = () => {
  console.log("" +
    "\n\n\n console.log print:\n"
    + "=================================\n"
    + "|| This log is from JavaScript. ||"
    + "\n=================================\n");
};

const testTimeOut = async () => {
  const sleep = (ms) => {
    return new Promise(resolve => setTimeout(() => {
      resolve(`return this value after ${ms}ms.`);
    }, ms));
  };
  return sleep(3000);
};

const testNestedPromise = () => {
  return new Promise(async (resolve, reject) => {
    testPromise().then((sumResult) => {
      resolve('got result:' + sumResult);
    });
    
  });
};

module.exports = {
  testConsoleLog, testPromise, testTimeOut, testNestedPromise
};