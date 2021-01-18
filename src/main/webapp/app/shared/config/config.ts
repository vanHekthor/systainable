import Vuex from 'vuex';
import { setupAxiosInterceptors } from '@/shared/config/axios-interceptor';

import { library } from '@fortawesome/fontawesome-svg-core';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons/faArrowLeft';
import { faAsterisk } from '@fortawesome/free-solid-svg-icons/faAsterisk';
import { faBan } from '@fortawesome/free-solid-svg-icons/faBan';
import { faBars } from '@fortawesome/free-solid-svg-icons/faBars';
import { faBell } from '@fortawesome/free-solid-svg-icons/faBell';
import { faBook } from '@fortawesome/free-solid-svg-icons/faBook';
import { faCloud } from '@fortawesome/free-solid-svg-icons/faCloud';
import { faCog } from '@fortawesome/free-solid-svg-icons/faCog';
import { faCogs } from '@fortawesome/free-solid-svg-icons/faCogs';
import { faEye } from '@fortawesome/free-solid-svg-icons/faEye';
import { faFlag } from '@fortawesome/free-solid-svg-icons/faFlag';
import { faHdd } from '@fortawesome/free-solid-svg-icons/faHdd';
import { faHeart } from '@fortawesome/free-solid-svg-icons/faHeart';
import { faHome } from '@fortawesome/free-solid-svg-icons/faHome';
import { faList } from '@fortawesome/free-solid-svg-icons/faList';
import { faLock } from '@fortawesome/free-solid-svg-icons/faLock';
import { faPencilAlt } from '@fortawesome/free-solid-svg-icons/faPencilAlt';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import { faRoad } from '@fortawesome/free-solid-svg-icons/faRoad';
import { faSave } from '@fortawesome/free-solid-svg-icons/faSave';
import { faSearch } from '@fortawesome/free-solid-svg-icons/faSearch';
import { faSignInAlt } from '@fortawesome/free-solid-svg-icons/faSignInAlt';
import { faSignOutAlt } from '@fortawesome/free-solid-svg-icons/faSignOutAlt';
import { faSort } from '@fortawesome/free-solid-svg-icons/faSort';
import { faSortDown } from '@fortawesome/free-solid-svg-icons/faSortDown';
import { faSortUp } from '@fortawesome/free-solid-svg-icons/faSortUp';
import { faSync } from '@fortawesome/free-solid-svg-icons/faSync';
import { faTachometerAlt } from '@fortawesome/free-solid-svg-icons/faTachometerAlt';
import { faTasks } from '@fortawesome/free-solid-svg-icons/faTasks';
import { faThList } from '@fortawesome/free-solid-svg-icons/faThList';
import { faTimesCircle } from '@fortawesome/free-solid-svg-icons/faTimesCircle';
import { faTimes } from '@fortawesome/free-solid-svg-icons/faTimes';
import { faTrash } from '@fortawesome/free-solid-svg-icons/faTrash';
import { faUserPlus } from '@fortawesome/free-solid-svg-icons/faUserPlus';
import { faUser } from '@fortawesome/free-solid-svg-icons/faUser';
import { faWrench } from '@fortawesome/free-solid-svg-icons/faWrench';

import { faCompass } from '@fortawesome/free-solid-svg-icons/faCompass';
import { faCopy } from '@fortawesome/free-solid-svg-icons/faCopy';
import { faEdit } from '@fortawesome/free-solid-svg-icons/faEdit';
import { faFileExport } from '@fortawesome/free-solid-svg-icons/faFileExport';
import { faFileUpload } from '@fortawesome/free-solid-svg-icons/faFileUpload';

import VueCookie from 'vue-cookie';
import Vuelidate from 'vuelidate';
import Vue2Filters from 'vue2-filters';

import * as filters from '@/shared/date/filters';
import { accountStore } from '@/shared/config/store/account-store';
import { alertStore } from '@/shared/config/store/alert-store';

export function initVueApp(vue) {
  vue.use(VueCookie);
  vue.use(Vuelidate);
  vue.use(Vue2Filters);
  setupAxiosInterceptors(() => console.log('Unauthorized!'));
  filters.initFilters();

  // custom directives
  vue.directive('click-outside', {
    bind(el, binding, vnode) {
      el.clickOutsideEvent = function checkClick(event) {
        // check if click was outside the el and its children
        if (!(el === event.target || el.contains(event.target))) {
          // if it was, call method provided in attribute value
          vnode.context[binding.expression](event);
        }
      };
      document.body.addEventListener('click', el.clickOutsideEvent);
    },
    unbind(el) {
      document.body.removeEventListener('click', el.clickOutsideEvent);
    },
  });
}

export function initFortAwesome(vue) {
  library.add(
    faArrowLeft,
    faAsterisk,
    faBan,
    faBars,
    faBell,
    faBook,
    faCloud,
    faCogs,
    faEye,
    faFlag,
    faHdd,
    faHeart,
    faHome,
    faList,
    faLock,
    faPencilAlt,
    faPlus,
    faRoad,
    faSave,
    faSearch,
    faSignInAlt,
    faSignOutAlt,
    faSort,
    faSortDown,
    faSortUp,
    faSync,
    faTachometerAlt,
    faTasks,
    faThList,
    faTimes,
    faTimesCircle,
    faTrash,
    faUser,
    faUserPlus,
    faWrench,

    // added:
    faCompass,
    faCopy,
    faEdit,
    faFileExport,
    faFileUpload,
    faPlus,
    faCog
  );
}

export function initVueXStore(vue) {
  vue.use(Vuex);
  return new Vuex.Store({
    modules: {
      accountStore,
      alertStore,
    },
  });
}
