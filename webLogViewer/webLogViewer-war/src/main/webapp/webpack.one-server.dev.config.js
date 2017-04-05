var webpack = require("webpack");
var path = require("path");
var jspath = "src/js/";

module.exports = {
    entry: {
        "bundle": "./src/entry.js"
    },
    output: {
        path: path.resolve(__dirname, "dist"),
        filename: "[name].js",
        library: ""
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"]
            },
            { 
                test: /.(png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot)$/,
                use: "url-loader?limit=100000"
            }
        ]
    },
    resolve: {
        extensions: [".js"],
        alias: {
            "Globals": path.resolve(jspath, "./commons/globals.js"),
            "Logger": path.resolve(jspath, "./logging/logger.js"),
            "Handlers": path.resolve(jspath, "./handlers/"),
            "Helpers": path.resolve(jspath, "./helpers/"),
            "Db": path.resolve(jspath, "./db/"),
            "Api": path.resolve(jspath, "./api/"),
            "Dto": path.resolve(jspath, "./dto/"),
            "Components": path.resolve(jspath, "./components/"),
            "Jobs": path.resolve(jspath, "./jobs/"),
            "Models": path.resolve(jspath, "./models/"),
            "Mappers": path.resolve(jspath, "./mappers/")
        }
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: "jquery",
            Globals: "Globals",
            Logger: "Logger"
        }),
        new webpack.DefinePlugin({
            "process.env.NODE_ENV": JSON.stringify(process.env.NODE_ENV || "external-server-dev"),
            __APIROOT__: JSON.stringify(""),
            __APICONTEXT__: JSON.stringify("webLogViewer-api")
        })
    ],
    node: {
        fs: "empty"
    }
};