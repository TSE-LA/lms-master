!function(e){var t={};function n(r){if(t[r])return t[r].exports;var i=t[r]={i:r,l:!1,exports:{}};return e[r].call(i.exports,i,i.exports,n),i.l=!0,i.exports}n.m=e,n.c=t,n.d=function(e,t,r){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var i in e)n.d(r,i,function(t){return e[t]}.bind(null,i));return r},n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="",n(n.s=0)}([function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=new(n(1).JarvisQuizWrapper);window.addEventListener("load",(function(){r.init(),null!=document.getElementById("submit-button")&&document.getElementById("submit-button").addEventListener("click",(function(){r.submit()}))}))},function(e,t,n){"use strict";var r,i=this&&this.__extends||(r=function(e,t){return(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])})(e,t)},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),o=this&&this.__values||function(e){var t="function"==typeof Symbol&&Symbol.iterator,n=t&&e[t],r=0;if(n)return n.call(e);if(e&&"number"==typeof e.length)return{next:function(){return e&&r>=e.length&&(e=void 0),{value:e&&e[r++],done:!e}}};throw new TypeError(t?"Object is not iterable.":"Symbol.iterator is not defined.")};Object.defineProperty(t,"__esModule",{value:!0});var s=n(2),l=n(5),a=n(6),c=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.overallScore=0,t.selectedAnswers=new Map,t.selectedMultipleChoiceAnswers=new Map,t}return i(t,e),t.prototype.init=function(){var e=this;this.getApi(),void 0!==this.API?(this.initialized=!0,this.setValue("cmi.exit","normal"),this.loadAssetData("quiz-data.json",(function(t){var n,r=new ldBar("#ld"),i=JSON.parse(t),o=parseInt(e.getValue("cmi.interactions._count"),10),s="unknown"===e.getValue("cmi.score.raw")?"0":e.getValue("cmi.score.raw");if(e.currentScore=parseInt(s,10),e.remainingAttempts=i.maxAttempts-o,e.thresholdScore=i.thresholdScore,e.passed=e.currentScore>=e.thresholdScore,e.setValue("cmi.completion_threshold",e.remainingAttempts<0?"0":e.remainingAttempts.toString()),e.remainingAttempts<=0){e.noAttemptsLeft=!0,n=0;var a=document.getElementById("submit-button");a.parentNode.removeChild(a)}else n=e.remainingAttempts;if(document.getElementById("current-score-value").innerText=e.currentScore.toString(),document.getElementById("attempts-value").innerText=n,document.getElementById("threshold-score-value").innerText=e.thresholdScore.toString(),e.noAttemptsLeft)e.createNoAttemptsPage();else for(var c=function(t){var n=i.data[t],o=[];n.answers.forEach((function(t){o.push({value:t.value,isCorrect:t.isCorrect,score:t.score}),t.isCorrect&&(e.overallScore+=t.score)}));var s=new l.Question(n.question,o,n.questionType);e.createQuestionFrame(s,t+1);var a=(t+1)/i.data.length*100;r.set(a)},u=0;u<i.data.length;u++)c(u);var d=document.getElementById("loader");d.parentNode.removeChild(d)}))):this.fail("Could not initialize communication with the LMS")},t.prototype.end=function(){},t.prototype.submit=function(){var e=0;this.selectedAnswers.forEach((function(t,n){t.isCorrect&&(e+=t.score)})),this.selectedMultipleChoiceAnswers.forEach((function(t,n){var r,i,s=0;try{for(var l=o(t),a=l.next();!a.done;a=l.next()){var c=a.value;c.isCorrect?s+=c.score:s-=c.score}}catch(e){r={error:e}}finally{try{a&&!a.done&&(i=l.return)&&i.call(l)}finally{if(r)throw r.error}}e+=s<=0?0:s})),this.currentScore=e>this.currentScore?e:this.currentScore,1===this.remainingAttempts&&this.currentScore<this.thresholdScore?this.setValue("cmi.success_status","failed"):e>=this.thresholdScore&&this.setValue("cmi.success_status","passed"),this.setValue("cmi.suspend_data",e.toString()),this.setValue("cmi.score.max",this.overallScore.toString()),this.setValue("cmi.score.raw",this.currentScore.toString()),this.setValue("cmi.progress_measure","100.0"),this.setValue("cmi.completion_status","completed")},t.prototype.createQuestionFrame=function(e,t){var n=document.createElement("section");n.setAttribute("class","section--center mdl-grid mdl-grid--no-spacing mdl-shadow--2dp");var r=document.createElement("div");r.setAttribute("class","mdl-card mdl-cell mdl-cell--12-col");var i=document.createElement("div");i.setAttribute("class","mdl-card__supporting-text mdl-grid mdl-grid--no-spacing");var o=document.createElement("h5");o.setAttribute("class","mdl-cell mdl-cell--12-col title-size"),o.innerText=t+". "+e.getTitle(),i.appendChild(o);var s=e.getAnswers();if(e.type===a.QuestionType.SINGLE_CHOICE)for(var l=0;l<s.length;l++)this.createAnswerRadioButton(s[l],i,t);else for(l=0;l<s.length;l++)this.createAnswerCheckbox(s[l],i,t);r.appendChild(i),n.appendChild(r),document.getElementById("quizFrame").appendChild(n)},t.prototype.createAnswerRadioButton=function(e,t,n){var r=this,i=document.createElement("div");i.setAttribute("class","section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone flex"),i.style.marginBottom="5px";var o=document.createElement("label"),s=document.createElement("input");s.setAttribute("type","radio"),s.setAttribute("class","with-gap"),s.setAttribute("name",n.toString()),s.checked=this.noAttemptsLeft&&e.isCorrect,s.disabled=this.noAttemptsLeft&&!e.isCorrect,s.addEventListener("change",(function(){r.selectedAnswers.set(n,e)}));var l=document.createElement("span");if(l.innerText=e.value,l.style.fontSize="16px",o.appendChild(s),o.appendChild(l),i.appendChild(o),this.noAttemptsLeft&&e.isCorrect){var a=document.createElement("div");a.setAttribute("class","correct-answer");var c=document.createElement("i");c.setAttribute("class","material-icons correct"),c.innerText="checked",a.appendChild(c),i.appendChild(a)}t.appendChild(i)},t.prototype.createAnswerCheckbox=function(e,t,n){var r=this,i=document.createElement("div");i.setAttribute("class","section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone flex"),i.style.marginBottom="5px";var s=document.createElement("label"),l=document.createElement("input");l.setAttribute("type","checkbox"),l.setAttribute("class","with-gap"),l.setAttribute("name",n.toString()),null!=this.selectedMultipleChoiceAnswers.get(n)&&null!=this.selectedMultipleChoiceAnswers.get(n).find((function(t){return t.value===e.value}))&&(l.checked=!0),l.addEventListener("change",(function(t){var i,s,l,a=!1;if(t.target.checked)if(null==(l=r.selectedMultipleChoiceAnswers.get(n))||l===[])r.selectedMultipleChoiceAnswers.set(n,[]),r.selectedMultipleChoiceAnswers.get(n).push(e);else{try{for(var c=o(l),u=c.next();!u.done;u=c.next()){var d=u.value;if(d&&d.value===e.value){a=!0;break}}}catch(e){i={error:e}}finally{try{u&&!u.done&&(s=c.return)&&s.call(c)}finally{if(i)throw i.error}}!1===a&&r.selectedMultipleChoiceAnswers.get(n).push(e)}else if(null!=(l=r.selectedMultipleChoiceAnswers.get(n)))for(var p=0;p<l.length;p++)l[p].value===e.value&&r.selectedMultipleChoiceAnswers.get(n).splice(p,1)}));var a=document.createElement("span");if(a.innerText=e.value,a.style.fontSize="18px",s.appendChild(l),s.appendChild(a),i.appendChild(s),this.noAttemptsLeft&&e.isCorrect){var c=document.createElement("div");c.setAttribute("class","correct-answer");var u=document.createElement("i");u.setAttribute("class","material-icons correct"),u.innerText="checked",c.appendChild(u),i.appendChild(c)}t.appendChild(i)},t.prototype.createNoAttemptsPage=function(){var e=document.createElement("section");e.setAttribute("class","section--center mdl-grid mdl-grid--no-spacing mdl-shadow--2dp");var t=document.createElement("div");t.setAttribute("class","mdl-card mdl-cell mdl-cell--12-col");var n,r,i=document.createElement("div");i.setAttribute("class","mdl-card__supporting-text mdl-grid mdl-grid--no-spacing"),i.setAttribute("style","justify-content: center;"),this.passed?(n="check-mark.png",r="ТЭНЦСЭН"):(n="x-mark.png",r="ТЭНЦЭЭГҮЙ");var o=document.createElement("img");o.setAttribute("src",n),o.setAttribute("style","width: 100px; margin-bottom: 16px;"),i.appendChild(o);var s=document.createElement("h4");s.setAttribute("class","mdl-cell mdl-cell--12-col title-size"),s.innerText=r,i.appendChild(s);var l=document.createElement("h5");l.setAttribute("class","mdl-cell mdl-cell--12-col title-size"),l.innerText="Тестийн оролдлого дууссан байна, та дараагийн тестэндээ идэвхтэй оролцоно уу! Баярлалаа",i.appendChild(l),t.appendChild(i),e.appendChild(t),document.getElementById("quizFrame").appendChild(e)},t}(s.BaseScormWrapper);t.JarvisQuizWrapper=c},function(e,t,n){"use strict";var r=this&&this.__values||function(e){var t="function"==typeof Symbol&&Symbol.iterator,n=t&&e[t],r=0;if(n)return n.call(e);if(e&&"number"==typeof e.length)return{next:function(){return e&&r>=e.length&&(e=void 0),{value:e&&e[r++],done:!e}}};throw new TypeError(t?"Object is not iterable.":"Symbol.iterator is not defined.")};Object.defineProperty(t,"__esModule",{value:!0});var i=n(3),o=function(){function e(){this.maxAttempts=500,this.initialized=!1,this.terminated=!1}return e.prototype.getApi=function(){null!=window.parent&&window.parent!==window&&(this.API=this.scanApi(window.parent)),null==this.API&&null!=window.opener&&(this.API=this.scanApi(window.opener))},e.prototype.scanApi=function(e){for(var t=0;null==e.API_1484_11&&null!=e.parent&&e.parent!==e;){if(++t>this.maxAttempts)return null;e=e.parent}return this.WINDOW=e,e.API_1484_11},e.prototype.getValue=function(e){return!1!==this.initialized&&!0!==this.terminated||this.fail("Connection is not established with the LMS"),this.API.GetValue(e)},e.prototype.setValue=function(e,t){!1!==this.initialized&&!0!==this.terminated||this.fail("Connection is not established with the LMS"),this.API.SetValue(e,t)},e.prototype.fail=function(e){var t=this.API.GetLastError(),n=this.API.GetErrorString(t),r=this.API.GetDiagnostic(t);alert(e+"\n"+("Error number: "+t+"\nDescription: "+n+"\nDiagnostic: "+r))},e.prototype.loadAssetData=function(e,t){var n=new XMLHttpRequest;n.overrideMimeType("application/json"),n.open("GET",e,!1),n.onreadystatechange=function(){4===n.readyState&&200===n.status?t(n.responseText):400===n.status&&alert("No asset data written on the json")},n.send(null)},e.convertToSCORMTime=function(e){var t,n,r,i,o,s,l,a="";return t=Math.floor(e/10),t-=315576e4*(l=Math.floor(t/315576e4)),t-=26298e4*(s=Math.floor(t/26298e4)),t-=864e4*(o=Math.floor(t/864e4)),t-=36e4*(i=Math.floor(t/36e4)),t-=6e3*(r=Math.floor(t/6e3)),l>0&&(a+=l+"Y"),s>0&&(a+=s+"M"),o>0&&(a+=o+"D"),(t-=100*(n=Math.floor(t/100)))+n+r+i>0&&(a+="T",i>0&&(a+=i+"H"),r>0&&(a+=r+"M"),t+n>0&&(a+=n,t>0&&(a+="."+t),a+="S")),""===a&&(a="0S"),a="P"+a},e.prototype.collectAssets=function(e){var t,n;if(null==e)return[];var o=[];try{for(var s=r(e),l=s.next();!l.done;l=s.next()){var a=l.value,c=a.substr(a.lastIndexOf(".")+1,a.length),u=a,d=i.AssetUtil.getAssetType(c.toUpperCase());o.push({assetName:u,type:d})}}catch(e){t={error:e}}finally{try{l&&!l.done&&(n=s.return)&&n.call(s)}finally{if(t)throw t.error}}return o},e}();t.BaseScormWrapper=o},function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=n(4),i=function(){function e(){}return e.getAssetType=function(e){return this.images.indexOf(e)>-1?r.AssetType.IMAGE:this.videos.indexOf(e)>-1?r.AssetType.VIDEO:this.web.indexOf(e)>-1?r.AssetType.HTML:r.AssetType.UNSUPPORTED},e.images=["JPEG","JPG","PNG"],e.videos=["MP4","WEBM"],e.web=["HTML"],e}();t.AssetUtil=i},function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),function(e){e.IMAGE="Image",e.VIDEO="Video",e.HTML="HTML",e.UNSUPPORTED="Unsupported"}(t.AssetType||(t.AssetType={}))},function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=function(){function e(e,t,n){var r=this;this.overallScore=0,this.score=0,this.title=e,this.answers=t,this.type=n,this.answers.forEach((function(e){e.isCorrect&&(r.overallScore+=1)}))}return e.prototype.getTitle=function(){return this.title},e.prototype.getAnswers=function(){return this.answers},e}();t.Question=r},function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),function(e){e.MULTI_CHOICE="MULTI_CHOICE",e.SINGLE_CHOICE="SINGLE_CHOICE",e.FILL_IN_BLANK="FILL_IN_BLANK"}(t.QuestionType||(t.QuestionType={}));var r=function(){function e(e,t,n,r){this.title=e,this.answers=t,this.type=n,this.isRequired=r}return e.prototype.getType=function(){return this.type},e.prototype.getTitle=function(){return this.title},e.prototype.getAnswers=function(){return this.answers},e}();t.Question=r}]);