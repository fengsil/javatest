<script setup>
defineProps({
  status: { type: Object, required: true },
  vueVersion: { type: String, default: '' },
  checking: { type: Boolean, default: false }
})
defineEmits(['refresh'])
</script>

<template>
  <div class="panel">
    <h1>系统检测面板</h1>
    <div class="hello">{{ status.hello || 'hello world' }}</div>

    <h2 class="section-title">运行状态</h2>
    <div class="status-item ok">
      <span class="name"><span class="dot green"></span>Vue 前端 ({{ vueVersion }})</span>
      <span class="badge ok">运行正常</span>
    </div>
    <div class="status-item" :class="status.springBoot === 'ok' ? 'ok' : 'fail'">
      <span class="name"><span class="dot blue"></span>Spring Boot 后端 (8080)</span>
      <span class="badge" :class="status.springBoot === 'ok' ? 'ok' : 'fail'">
        {{ status.springBoot === 'ok' ? '连接成功' : '连接失败' }}
      </span>
    </div>
    <div class="status-item" :class="status.mybatis === 'ok' ? 'ok' : (status.mybatis === 'fail' ? 'fail' : '')">
      <span class="name"><span class="dot orange"></span>MyBatis ({{ status.database }})</span>
      <span class="badge" :class="status.mybatis === 'ok' ? 'ok' : (status.mybatis === 'fail' ? 'fail' : 'checking')">
        {{ status.mybatis === 'ok' ? '连接成功' : (status.mybatis === 'fail' ? '连接失败' : '检测中...') }}
      </span>
    </div>

    <div v-if="status.error" class="error-detail">{{ status.error }}</div>

    <div class="footer">
      检测时间：{{ status.checkedAt || '-' }} ｜ 每 2 秒自动刷新<br>
      接口：/api/hello、/api/status ｜ 后端关闭后会自动返回启动界面
      <button class="btn" @click="$emit('refresh')" :disabled="checking">
        {{ checking ? '检测中...' : '立即刷新' }}
      </button>
    </div>
  </div>
</template>
