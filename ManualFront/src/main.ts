import 'ant-design-vue/dist/reset.css'
import './assets/main.css'

import Antd from 'ant-design-vue'
import { createApp } from 'vue'

import App from './App.vue'
import router from './router'
import { pinia } from './stores'

const app = createApp(App)

app.use(Antd)
app.use(pinia)
app.use(router)

app.mount('#app')
