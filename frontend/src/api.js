// 前端只通过 /api 访问后端，Vite 会把它代理到 http://localhost:8080
// 若后端未启动，请求会失败，页面据此停留在启动界面
const API_BASE = '/api'
const TIMEOUT_MS = 2000

async function fetchJson(path) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), TIMEOUT_MS)
  try {
    const res = await fetch(API_BASE + path, {
      signal: controller.signal,
      cache: 'no-store'
    })
    if (!res.ok) throw new Error('HTTP ' + res.status)
    return await res.json()
  } finally {
    clearTimeout(timer)
  }
}

export async function checkBackend() {
  const result = {
    springBoot: 'fail',
    mybatis: 'fail',
    database: '-',
    hello: '',
    error: '',
    checkedAt: ''
  }

  try {
    const hello = await fetchJson('/hello')
    result.springBoot = 'ok'
    result.hello = hello.message
  } catch (e) {
    result.error = e.name === 'AbortError'
      ? '连接超时：Spring Boot（8080）未响应'
      : '无法连接 Spring Boot（8080）：' + e.message
    result.checkedAt = new Date().toLocaleTimeString()
    return result
  }

  try {
    const status = await fetchJson('/status')
    result.mybatis = status.mybatis ? 'ok' : 'fail'
    result.database = status.database || '-'
    if (status.mybatisError) result.error = status.mybatisError
  } catch (e) {
    result.error = '状态接口不可连接：' + e.message
  }

  result.checkedAt = new Date().toLocaleTimeString()
  return result
}
