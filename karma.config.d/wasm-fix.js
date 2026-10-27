// Timeouts
config.client = config.client || {};
config.client.mocha = config.client.mocha || {};
config.client.mocha.timeout = 600000;

config.browserNoActivityTimeout = 600000;
config.browserDisconnectTimeout = 600000;
config.pingTimeout = 600000;

// Custom Launchers
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

// Webpack fixes
if (config.webpack) {
    if (config.webpack.resolveLoader && Array.isArray(config.webpack.resolveLoader.modules)) {
        config.webpack.resolveLoader.modules = config.webpack.resolveLoader.modules.filter(Boolean);
    }
    const webpack = require('webpack');
    config.webpack.plugins = config.webpack.plugins || [];
    config.webpack.plugins.push(
        new webpack.DefinePlugin({
            'import.meta': JSON.stringify({ url: 'http://localhost/' }),
            'import.meta.url': JSON.stringify('http://localhost/')
        })
    );
}
