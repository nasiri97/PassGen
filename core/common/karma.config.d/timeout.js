config.client = config.client || {};
config.client.mocha = config.client.mocha || {};
config.client.mocha.timeout = 120000;

config.browserNoActivityTimeout = 120000;
config.browserDisconnectTimeout = 30000;
config.pingTimeout = 30000;

config.customLaunchers = config.customLaunchers || {};
config.customLaunchers.ChromeHeadlessNoSandbox = {
  base: 'ChromeHeadless',
  flags: [
    '--no-sandbox',
    '--disable-setuid-sandbox',
    '--disable-gpu',
    '--disable-dev-shm-usage'
  ]
};
config.browsers = ['ChromeHeadlessNoSandbox'];
