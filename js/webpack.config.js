const path = require('path');

module.exports = {
  entry: './src/example/index.js',
  output: {
    library: 'libExample',
    path: path.resolve(__dirname, 'dist'),
    filename: 'example.js',
  }, optimization: {
    minimize: false
  }, module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env']
          }
        }
      }
    ]
  }
};