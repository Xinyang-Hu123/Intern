<template>
  <div :class="[{'is-active': isActive}]" @click="toggleClick">
    <svg
      class="hamburger-icon"
      width="20"
      height="20"
      viewBox="0 0 1024 1024"
      aria-hidden="true"
      focusable="false"
    >
      <path d="M128 213.3h768v85.4H128zM128 469.3h768v85.4H128zM128 725.3h768v85.4H128z" />
    </svg>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'

@Component({
  'name': 'Hamburger'
})

export default class extends Vue {
  @Prop({ 'default': false }) private isActive!: boolean

  private toggleClick() {
    this.$emit('toggleClick');
  }
}
</script>

<style lang="scss" scoped>
.hamburger-icon {
  vertical-align: middle;
  fill: currentColor;
}

.is-active {
  transform: rotate(180deg);
}
</style>
