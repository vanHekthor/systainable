import { createApp } from 'vue'
import App from './App.vue'
import 'primevue/resources/themes/bootstrap4-light-purple/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css';


const app = createApp(App);
app.config.globalProperties.$primevue = {ripple: true};
app.mount('#app')
