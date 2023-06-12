!function(t){var e={};function n(i){if(e[i])return e[i].exports;var r=e[i]={i:i,l:!1,exports:{}};return t[i].call(r.exports,r,r.exports,n),r.l=!0,r.exports}n.m=t,n.c=e,n.d=function(t,e,i){n.o(t,e)||Object.defineProperty(t,e,{enumerable:!0,get:i})},n.r=function(t){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(t,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(t,"__esModule",{value:!0})},n.t=function(t,e){if(1&e&&(t=n(t)),8&e)return t;if(4&e&&"object"==typeof t&&t&&t.__esModule)return t;var i=Object.create(null);if(n.r(i),Object.defineProperty(i,"default",{enumerable:!0,value:t}),2&e&&"string"!=typeof t)for(var r in t)n.d(i,r,function(e){return t[e]}.bind(null,r));return i},n.n=function(t){var e=t&&t.__esModule?function(){return t.default}:function(){return t};return n.d(e,"a",e),e},n.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},n.p="",n(n.s=0)}([function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=new(n(1).UnitelQuestionnaireWrapper);window.addEventListener("load",(function(){i.init(),document.getElementById("submit-button").addEventListener("click",(function(){i.submit()}))}))},function(t,e,n){"use strict";var i,r=this&&this.__extends||(i=function(t,e){return(i=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var n in e)e.hasOwnProperty(n)&&(t[n]=e[n])})(t,e)},function(t,e){function n(){this.constructor=t}i(t,e),t.prototype=null===e?Object.create(e):(n.prototype=e.prototype,new n)});Object.defineProperty(e,"__esModule",{value:!0});var o=n(2),s=n(5),a=function(t){function e(){var e=null!==t&&t.apply(this,arguments)||this;return e.answer=" ",e}return r(e,t),e.prototype.init=function(){var t=this;this.getApi(),void 0!==this.API&&(this.initialized=!0,this.setValue("cmi.exit","normal")),this.loadAssetData("questionnaire-data.json",(function(e){var n=JSON.parse(e).data[0],i=new s.Question(n.question,null,s.QuestionType.FILL_IN_BLANK,!1);t.createQuestionFrame(i),t.setButtonEnability()})),this.setValue("cmi.progress_measure","100.0")},e.prototype.end=function(){},e.prototype.submit=function(){this.setValue("cmi.completion_status","completed"),this.setValue("cmi.comments_from_learner.1.comment",this.answer.toString())},e.prototype.createQuestionFrame=function(t){var e=document.createElement("section");e.setAttribute("class","section--center mdl-grid mdl-grid--no-spacing mdl-shadow--2dp");var n=document.createElement("div");n.setAttribute("class","mdl-card mdl-cell mdl-cell--12-col");var i=document.createElement("div");i.setAttribute("class","mdl-card__supporting-text mdl-grid mdl-grid--no-spacing");var r=document.createElement("h5");r.setAttribute("class","mdl-cell mdl-cell--12-col"),r.innerText=t.getTitle(),i.appendChild(r),this.createQuestionnaireBox(i),n.appendChild(i),e.appendChild(n),document.getElementById("testFrame").appendChild(e)},e.prototype.createQuestionnaireBox=function(t){var e=this,n=document.createElement("div");n.setAttribute("class","section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone"),n.style.marginBottom="5px";var i=document.createElement("label"),r=document.createElement("textarea");r.setAttribute("type","text"),r.setAttribute("class","with-gap"),r.setAttribute("maxlength","170"),this.getApi(),void 0!==this.API&&(this.initialized=!0);var o=this.getValue("cmi.comments_from_learner.1.comment");r.value="unknown"==o?"":o,r.addEventListener("input",(function(t){e.answer=t.target.value}));var s=document.createElement("span");s.style.fontSize="18px",i.appendChild(r),i.appendChild(s),n.appendChild(i),t.appendChild(n)},e.prototype.setButtonEnability=function(){"completed"==this.getValue("cmi.completion_status")&&(document.getElementById("#submit-button").disabled=!0)},e}(o.BaseScormWrapper);e.UnitelQuestionnaireWrapper=a},function(t,e,n){"use strict";var i=this&&this.__values||function(t){var e="function"==typeof Symbol&&Symbol.iterator,n=e&&t[e],i=0;if(n)return n.call(t);if(t&&"number"==typeof t.length)return{next:function(){return t&&i>=t.length&&(t=void 0),{value:t&&t[i++],done:!t}}};throw new TypeError(e?"Object is not iterable.":"Symbol.iterator is not defined.")};Object.defineProperty(e,"__esModule",{value:!0});var r=n(3),o=function(){function t(){this.maxAttempts=500,this.initialized=!1,this.terminated=!1}return t.prototype.getApi=function(){null!=window.parent&&window.parent!==window&&(this.API=this.scanApi(window.parent)),null==this.API&&null!=window.opener&&(this.API=this.scanApi(window.opener))},t.prototype.scanApi=function(t){for(var e=0;null==t.API_1484_11&&null!=t.parent&&t.parent!==t;){if(++e>this.maxAttempts)return null;t=t.parent}return this.WINDOW=t,t.API_1484_11},t.prototype.getValue=function(t){return!1!==this.initialized&&!0!==this.terminated||this.fail("Connection is not established with the LMS"),this.API.GetValue(t)},t.prototype.setValue=function(t,e){!1!==this.initialized&&!0!==this.terminated||this.fail("Connection is not established with the LMS"),this.API.SetValue(t,e)},t.prototype.fail=function(t){var e=this.API.GetLastError(),n=this.API.GetErrorString(e),i=this.API.GetDiagnostic(e);alert(t+"\n"+("Error number: "+e+"\nDescription: "+n+"\nDiagnostic: "+i))},t.prototype.loadAssetData=function(t,e){var n=new XMLHttpRequest;n.overrideMimeType("application/json"),n.open("GET",t,!1),n.onreadystatechange=function(){4===n.readyState&&200===n.status?e(n.responseText):400===n.status&&alert("No asset data written on the json")},n.send(null)},t.convertToSCORMTime=function(t){var e,n,i,r,o,s,a,l="";return e=Math.floor(t/10),e-=315576e4*(a=Math.floor(e/315576e4)),e-=26298e4*(s=Math.floor(e/26298e4)),e-=864e4*(o=Math.floor(e/864e4)),e-=36e4*(r=Math.floor(e/36e4)),e-=6e3*(i=Math.floor(e/6e3)),a>0&&(l+=a+"Y"),s>0&&(l+=s+"M"),o>0&&(l+=o+"D"),(e-=100*(n=Math.floor(e/100)))+n+i+r>0&&(l+="T",r>0&&(l+=r+"H"),i>0&&(l+=i+"M"),e+n>0&&(l+=n,e>0&&(l+="."+e),l+="S")),""===l&&(l="0S"),l="P"+l},t.prototype.collectAssets=function(t){var e,n;if(null==t)return[];var o=[];try{for(var s=i(t),a=s.next();!a.done;a=s.next()){var l=a.value,u=l.substr(l.lastIndexOf(".")+1,l.length),c=l,p=r.AssetUtil.getAssetType(u.toUpperCase());o.push({assetName:c,type:p})}}catch(t){e={error:t}}finally{try{a&&!a.done&&(n=s.return)&&n.call(s)}finally{if(e)throw e.error}}return o},t}();e.BaseScormWrapper=o},function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(4),r=function(){function t(){}return t.getAssetType=function(t){return this.images.indexOf(t)>-1?i.AssetType.IMAGE:this.videos.indexOf(t)>-1?i.AssetType.VIDEO:this.web.indexOf(t)>-1?i.AssetType.HTML:i.AssetType.UNSUPPORTED},t.images=["JPEG","JPG","PNG"],t.videos=["MP4","WEBM"],t.web=["HTML"],t}();e.AssetUtil=r},function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),function(t){t.IMAGE="Image",t.VIDEO="Video",t.HTML="HTML",t.UNSUPPORTED="Unsupported"}(e.AssetType||(e.AssetType={}))},function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),function(t){t.MULTI_CHOICE="MULTI_CHOICE",t.SINGLE_CHOICE="SINGLE_CHOICE",t.FILL_IN_BLANK="FILL_IN_BLANK"}(e.QuestionType||(e.QuestionType={}));var i=function(){function t(t,e,n,i){this.title=t,this.answers=e,this.type=n,this.isRequired=i}return t.prototype.getType=function(){return this.type},t.prototype.getTitle=function(){return this.title},t.prototype.getAnswers=function(){return this.answers},t}();e.Question=i}]);