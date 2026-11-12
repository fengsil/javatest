<script setup>
defineProps({
  status: { type: Object, default: null },
  vueVersion: { type: String, default: '' },
  checking: { type: Boolean, default: false }
})
defineEmits(['refresh'])
</script>

<template>
  <div class="panel">
    <div class="launch-head">
      <div class="launch-logo">V</div>
      <div>
        <h1>Vue 前端已启动</h1>
        <p class="subtitle">版本 {{ vueVersion }} ｜ 独立运行于 5173 端口</p>
      </div>
    </div>

    <div class="waiting-box">
      <div class="waiting-title">正在等待 Spring Boot（8080）启动</div>
      <div class="progress"><div class="progress-bar"></div></div>
      <p class="hint">
        检测面板已分离为独立界面：只有后端启动并能连通后才会显示；<br>
        未连通时这里只显示前端自身状态与当前可联系的功能。
      </p>
    </div>

    <h2 class="section-title">当前可联系的功能</h2>
    <div class="status-item ok">
      <span class="name"><span class="dot green"></span>Vue 前端</span>
      <span class="badge ok">已启动</span>
    </div>
    <div class="status-item fail">
      <span class="name"><span class="dot blue"></span>Spring Boot 后端 (8080)</span>
      <span class="badge fail">未连接</span>
    </div>
    <div class="status-item">
      <span class="name"><span class="dot orange"></span>MyBatis 数据库</span>
      <span class="badge checking">等待后端</span>
    </div>

    <div v-if="status?.error" class="error-detail">{{ status.error }}</div>

    <div class="footer">
      检测时间：{{ status?.checkedAt || '-' }} ｜ 每 2 秒自动重试
      <button class="btn" @click="$emit('refresh')" :disabled="checking">
        {{ checking ? '检测中...' : '立即重试' }}
      </button>
    </div>
  </div>
</template>
