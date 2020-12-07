import axios from 'axios'

const SERVER_URL = 'http://localhost:8080';

const instance = axios.create({
    baseURL: SERVER_URL
});

export default {
    // GET
    // example functions
    getFeatures:
        function getFeatures() {
            instance.get('featuremodels/example/featuremodel1')
                .then((response) => {
                    console.log(response.data);
                    console.log(response.status);
                    console.log(response.statusText);
                    console.log(response.headers);
                    console.log(response.config);

                    return response.data;
                });
        },

    getConfigExample:
        function getConfigExample() {
            instance.get('configexample')
                .then((response) => {
                    console.log(response.data);
                    console.log(response.status);
                    console.log(response.statusText);
                    console.log(response.headers);
                    console.log(response.config);
                })
        },

    getProperties:
        function getProperties(config) {
            instance.get('property-prediction', config)
                .then((response) => {
                    console.log(response.data);
                    console.log(response.status);
                    console.log(response.statusText);
                    console.log(response.headers);
                    console.log(response.config);
                });
        }

}
