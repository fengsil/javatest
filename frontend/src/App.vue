<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { version } from 'vue'
import { checkBackend } from './api'
import StartupView from './views/StartupView.vue'
import DashboardView from './views/DashboardView.vue'

const vueVersion = version
const status = ref(null)
const checking = ref(true)
let timer = null

// 只有 Spring Boot（8080）连通后，检测面板才允许显示
const ready = computed(() => status.value?.springBoot === 'ok')

async function refresh() {
  checking.value = true
  status.value = await checkBackend()
  checking.value = false
}

onMounted(() => {
  refresh()
  timer = setInterval(refresh, 2000)
})

onUnmounted(() => clearInterval(timer))
</script>

<template>
  <DashboardView
      v-if="ready"
      :status="status"
      :vue-version="vueVersion"
      :checking="checking"
      @refresh="refresh"
  />
  <StartupView
      v-else
      :status="status"
      :vue-version="vueVersion"
      :checking="checking"
      @refresh="refresh"
  />
</template>
