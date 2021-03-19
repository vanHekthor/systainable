<template>
    <div @click="onClick">
        <div v-if="!editMode" class="p-2" style="cursor: pointer">
            <slot></slot>
        </div>
        <div v-else v-click-outside="onOutsideClick">
            <slot name="edit"></slot>
        </div>
    </div>
</template>

<script>
export default {
    name: "EditCell",
    data() {
        return {
            editMode: false,
        }
    },
    methods: {
      onOutsideClick() {
          this.editMode = false;
      },

      onClick() {
          this.editMode = true;
      }
    }
}
</script>

<style scoped>

</style>
