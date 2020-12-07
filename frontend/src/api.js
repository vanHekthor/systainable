import axios from 'axios'

const SERVER_URL = 'Backend-URL';

const instance = axios.create({
    baseURL: SERVER_URL;
});

export default {
    // GET
    // example functions
    function getFeatures() {
        instance.get('features-URL')
            .then((response) => {
                console.log(response.data);
                console.log(response.status);
                console.log(response.statusText);
                console.log(response.headers);
                console.log(response.config);
            });
    }

    function getProperties(config) {
        instance.get('system-property-prediction-URL', config)
            .then((response) => {
                console.log(response.data);
                console.log(response.status);
                console.log(response.statusText);
                console.log(response.headers);
                console.log(response.config);
            });
    }

}
