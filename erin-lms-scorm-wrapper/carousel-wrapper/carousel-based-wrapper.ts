import {BaseScormWrapper} from '../base-scorm-wrapper';
import {Asset} from '../asset.model';
import {SuspendData} from '../suspend_data';
import {Video} from '../video';
import {AssetType} from '../constants/asset-type';

export class CarouselBasedScormWrapper extends BaseScormWrapper {
    private assets: Asset[] = [];
    private startTimeStamp: Date;

    private completionStatus: string;
    private progressMeasure: number;
    private bookmark: number = 0;

    private suspend_data: SuspendData;

    private hasVideos: boolean = false;
    private hasImages: boolean = false;
    private isSuspendDataPopulated: boolean = false;
    private suspend_data_str: string;

    private videoCount = 0;
    private videos: Video[] = [];

    private imageIndexes: number[] = [];
    private endPage: number = 1;

    private swiper;
    private fullscreenContainer: HTMLElement;

    constructor() {
        super();
    }

    init(): void {
        this.getApi();

        if (this.API == null) {
            alert("Could not establish a connection with the LMS");
            return;
        }

        const isInitialized: boolean = this.API.Initialize("");

        if (!isInitialized) {
            this.fail("Could not initialize communication with the LMS");
            return;
        }

        this.initialized = true;
        this.terminated = false;

        this.setValue("cmi.exit", "normal");

        this.loadAssetData('data.json', (response) => {
            let assetData = JSON.parse(response);
            let queryString = document.location.search;

            const searchValue = queryString.substr(queryString.indexOf("?"), queryString.indexOf("=") + 1);
            queryString = queryString.replace(searchValue, "");
            this.assets = this.collectAssets(assetData["data"][queryString]);

            this.completionStatus = this.getValue("cmi.completion_status");
            if (this.completionStatus === "not attempted" || this.completionStatus === "unknown") {
                this.setValue("cmi.completion_status", "incomplete");
            }

            this.bookmark = parseInt(this.getValue("cmi.location"), 10);
            this.progressMeasure = parseFloat(this.getValue("cmi.progress_measure"));
            this.suspend_data_str = this.getValue("cmi.suspend_data");
            if (this.suspend_data_str.indexOf('Â') != -1) {
                this.suspend_data_str = 'unknown';
                this.progressMeasure = 0;
            }
            this.isSuspendDataPopulated = !(this.suspend_data_str === undefined || this.suspend_data_str == null || this.suspend_data_str === 'unknown');

            this.loadAssets();
            if (this.assets.length === 1 && !this.hasVideos) {
                this.setProgressMeasure(100);
            }
            this.startTimeStamp = new Date();
        });
    }

    end(): void {
        if ((!this.initialized) || this.terminated) {
            return;
        }

        const endTimeStamp = new Date();
        const totalMilliseconds = (endTimeStamp.getTime() - this.startTimeStamp.getTime());

        const scormTime = BaseScormWrapper.convertToSCORMTime(totalMilliseconds);

        this.setValue("cmi.session_time", scormTime);
        this.setValue("cmi.exit", "suspend");

        const isTerminated: boolean = this.API.Terminate("");
        if (!isTerminated) {
            // this.fail("Could not terminate communication with the LMS");
        }
    }

    // @ts-ignore
    private async loadAssets(): void {
        this.swiper = this.getSwiper();
        //@ts-ignore
        const loader = new ldBar('#ld');

        for (let index = 0; index < this.assets.length; index++) {
            const currentAsset = this.assets[index];

            if (currentAsset.type === AssetType.IMAGE) {
                this.hasImages = true;
                this.imageIndexes.push(index);
            } else if (currentAsset.type === AssetType.VIDEO) {
                this.hasVideos = true;
            }

            await this.loadAsset(currentAsset).then((loadedAsset: HTMLElement) => {
                this.swiper.appendSlide(this.createSwiperSlide(loadedAsset));
                const isLastAsset = index + 1 === this.assets.length;

                if (isLastAsset) {
                    this.swiper.on('slideChange', () => this.onSlideChange());

                    if (this.hasVideos) {
                        this.loadVideos();
                    }
                }
            });

            const loaderProgress = (index + 1) / (this.assets.length) * 100;
            loader.set(loaderProgress);
        }

        const loaderElem = document.getElementById('loader');
        loaderElem.parentNode.removeChild(loaderElem);

        if (this.isSuspendDataPopulated) {
            this.suspend_data = new SuspendData(this.suspend_data_str);
            this.endPage = this.suspend_data.endPage;
        } else {
            this.suspend_data = new SuspendData(undefined);
        }

        this.fullscreenContainer = document.getElementById('fullscreen-container');

        if (this.imageIndexes.indexOf(0) < 0 || this.imageIndexes.indexOf(this.bookmark) < 0) {
            this.hideFullscreen();
        }

        //@ts-ignore
        this.swiper.init();

        const bullets = document.getElementsByClassName('bullet');
        for (let index = 0; index < bullets.length; index++) {
            bullets[index].addEventListener('click', () => this.onBulletClick(index));
        }

        this.addWindowEventListener(this.WINDOW);
        this.addWindowEventListener(window);

        ["fullscreenchange", "webkitfullscreenchange", "mozfullscreenchange", "msfullscreenchange"].forEach(
            eventType => document.addEventListener(eventType, () => {
                const icon = document.getElementById('icon');

                if (icon.innerText === 'fullscreen') {
                    icon.innerText = 'fullscreen_exit';
                    icon.classList.remove('hvr-shutter-out-vertical');
                    icon.classList.add('fullscreen-exit');
                } else {
                    icon.innerText = 'fullscreen';
                    icon.classList.remove('fullscreen-exit');
                    icon.classList.add('hvr-shutter-out-vertical');
                }
            })
        );

        this.swiper.slideTo(this.bookmark, 500, true);
    }

    // @ts-ignore
    private async loadAsset(asset: Asset): Promise<any> {
        switch (asset.type) {
            case AssetType.IMAGE:
                // @ts-ignore
                return new Promise((resolve, reject) => {
                    let img = new Image();
                    img.addEventListener('load', event => resolve(img));
                    img.src = '../' + asset.assetName;
                });
            case AssetType.HTML:
                window.location.replace('../' + asset.assetName);
                break;
            case AssetType.VIDEO:
                // @ts-ignore
                return new Promise((resolve, reject) => {
                    const video = document.createElement('video');
                    video.controls = true;
                    video.width = 640;
                    video.height = 264;
                    video.style.marginBottom = "32px";
                    video.setAttribute("class", "video-js vjs-big-play-centered");
                    video.setAttribute("preload", "auto");
                    video.setAttribute("data-setup", '{"aspectRatio":"16:9", "playbackRates": [1, 1.5, 2] }');

                    const source = document.createElement('source');
                    const assetName = asset.assetName;
                    const mimeType = assetName.substr(assetName.lastIndexOf('.') + 1, assetName.length);
                    source.type = 'video/' + mimeType;
                    let currentLocation = window.location.href.substring(0, window.location.href.lastIndexOf("/"));
                    currentLocation = currentLocation.substring(0, currentLocation.lastIndexOf("/"));
                    const videoUrl = currentLocation + '/' + asset.assetName;

                    source.src = 'http://localhost/content/stream-video-by-url?url=' + encodeURIComponent(videoUrl);

                    video.title = assetName;
                    video.appendChild(source);
                    resolve(video);
                });
            default:
                return null;
        }
    }

    private onSlideChange(): void {
        const videos: HTMLCollectionOf<HTMLVideoElement> = document.getElementsByTagName("video");

        for (let index = 0; index < videos.length; index++) {
            const video = videos[index];
            video.setAttribute("id", "video" + index);
            this.pauseVideo(video);
        }
        const pagination = document.getElementsByClassName('pagination');
        pagination[0].classList.add('hovered');

        setTimeout(() => {
            pagination[0].classList.remove('hovered');
        }, 2500);

        const currentIndex = this.swiper.activeIndex;
        const isCurrentAssetImage = this.imageIndexes.indexOf(currentIndex) > -1;

        if (isCurrentAssetImage) {
            this.unhideFullscreen();
        } else {
            this.hideFullscreen();
        }

        if (this.hasImages && isCurrentAssetImage) {
            const imageIndex = this.imageIndexes.indexOf(currentIndex);
            if (this.videos.length == 0) {
                this.suspend_data = null;
            }
            const progressMeasure = (imageIndex + 1) / this.imageIndexes.length * 100 *
                (this.suspend_data != null ? this.suspend_data.textProgressWeight : 1);

            if (progressMeasure > this.progressMeasure) {
                this.progressMeasure = progressMeasure;
                this.setProgressMeasure(this.progressMeasure);

                if (this.hasVideos && this.suspend_data.hasCompletedVideos()
                    && Math.round(this.progressMeasure / this.suspend_data.textProgressWeight) >= 100) {
                    this.setProgressMeasure(100);
                }
            }
        }

        if (currentIndex + 1 > this.endPage) {
            this.endPage = currentIndex + 1;
            this.suspend_data = new SuspendData(undefined);
            this.suspend_data.setEndPage(this.endPage);
            this.setSuspendData(this.suspend_data.toString());
            this.activateBullet(currentIndex);
        }

        this.setValue("cmi.location", currentIndex);
    }

    private loadVideos(): void {
        const videos: HTMLCollectionOf<HTMLVideoElement> = document.getElementsByTagName("video");

        this.videoCount = videos.length;

        for (let index = 0; index < videos.length; index++) {
            const video = videos[index];
            video.setAttribute("id", "video" + index);

            if (this.isSuspendDataPopulated) {
                this.onVideoLoaded(video);
            } else {
                this.preloadVideo(video);
            }
        }
    }

    private pauseVideo(video: HTMLElement): void {
        // @ts-ignore
        const player = videojs(video.id);
        player.pause();
    }

    private preloadVideo(video: HTMLElement): void {
        // @ts-ignore
        const player = videojs(video.id);
        if (player.readyState() === undefined) {
            player.on('loadedmetadata', () => {
                this.preloadPlayer(player, video.title);
            });
        } else {
            this.preloadPlayer(player, video.title);
        }
    }

    private preloadPlayer(player: any, videoName: string): void {
        this.setVideos(player, videoName);

        this.onVideoPlayed(player, videoName);

        if (this.videoCount === 0) {
            this.createSuspendData();
        }
    }

    private onVideoLoaded(video: HTMLElement): void {
        // @ts-ignore
        const player = videojs(video.id);
        if (player.readyState() === undefined) {
            player.on('loadedmetadata', () => {
                this.setPlayerCurrentTime(player, video);
                this.setVideos(player, video.title);
            });
        } else {
            this.setPlayerCurrentTime(player, video)
            this.setVideos(player, video.title);
        }
        this.onVideoPlayed(player, video.title);

    }

    private setVideos(player: any, videoName: string) {
        this.videoCount--;
        const v = new Video(videoName, 0, 0, 0, false);
        v.setDuration(player.duration());
        this.videos.push(v);
    }

    private setPlayerCurrentTime(player: any, video: HTMLElement): void {
        if (this.isSuspendDataPopulated) {
            this.suspend_data = new SuspendData(this.suspend_data_str);
            const currentTime = this.suspend_data.getVideoCurrentTime(video.title);
            player.currentTime(currentTime);
        }
    }

    private onVideoPlayed(player: any, videoName: string): void {
        player.on('timeupdate', () => {
            if (this.isSuspendDataPopulated) {
                const isSuspendDataCorrect = this.suspend_data.updateVideoCurrentTime(videoName, player.currentTime());
                if (!isSuspendDataCorrect) {
                    this.suspendDataCorrector(isSuspendDataCorrect);
                    this.suspend_data.updateVideoCurrentTime(videoName, player.currentTime());
                }
                this.setSuspendData(this.suspend_data.toString());
                const currentProgress = player.currentTime() / player.duration() * 100;
                const savedProgress = this.suspend_data.getVideoProgressMeasure(videoName);
                if (currentProgress > savedProgress) {
                    this.suspend_data.updateVideoProgressMeasure(videoName, currentProgress);
                    this.setSuspendData(this.suspend_data.toString());
                }
            }
        });
        player.on('ended', () => {
            if (!this.suspend_data.isVideoCompleted(videoName)) {
                this.saveVideoProgress(videoName);
                this.suspend_data.completeVideo(videoName);
                this.setSuspendData(this.suspend_data.toString());

                let allVideosCompleted = true;
                for (const video of this.suspend_data.videos) {
                    if (!video.isCompleted) {
                        allVideosCompleted = false;
                        break;
                    }
                }

                if (allVideosCompleted && this.progressMeasure === 100 || allVideosCompleted && !this.hasImages) {
                    this.setProgressMeasure(100);
                }
            }
        });
    }

    private saveVideoProgress(videoName: string): void {
        const savedProgress = this.suspend_data.getVideoProgressMeasure(videoName);
        this.progressMeasure += savedProgress * this.suspend_data.getVideoProgressWeight(videoName);
        this.setProgressMeasure(this.progressMeasure);
    }

    private setProgressMeasure(progressMeasure: number): void {
        if (progressMeasure > 100) {
            progressMeasure = 100;
        }

        this.setValue("cmi.progress_measure", progressMeasure.toFixed(1));

        if (progressMeasure == 100) {
            this.setValue("cmi.completion_status", "completed");
        }
    }

    private setSuspendData(suspend_data: string): void {
        this.setValue("cmi.suspend_data", suspend_data);
    }

    private createSuspendData(): void {
        if (!this.isSuspendDataPopulated && this.videos.length > 0) {
            let pageCount = 0;
            for (let asset of this.assets) {
                if (asset.type === AssetType.IMAGE) {
                    pageCount++;
                }
            }

            const expectedDurationOfText = 200 * pageCount / 250 * 60;
            let overallExpectedDuration = expectedDurationOfText;

            for (let video of this.videos) {
                overallExpectedDuration += video.duration;
            }

            const textProgressWeight = expectedDurationOfText / overallExpectedDuration;
            let suspend_data_str = "textProgressWeight=" + textProgressWeight;

            for (let video of this.videos) {
                const videoWeight = video.duration / overallExpectedDuration;
                suspend_data_str += "," + video.videoName + "=0|0|" + videoWeight + "|false";
            }

            this.suspend_data_str = suspend_data_str;
            this.suspend_data = new SuspendData(this.suspend_data_str);
            this.isSuspendDataPopulated = true;
        }
    }

    private createSwiperSlide(asset: HTMLElement): HTMLElement {
        const swiperSlide = document.createElement('div');
        swiperSlide.setAttribute('class', 'swiper-slide');
        swiperSlide.appendChild(asset);
        return swiperSlide;
    }

    private getSwiper(): any {
        //@ts-ignore
        return new Swiper('.swiper-container', {
            init: false,
            spaceBetween: 50,
            pagination: {
                el: '.swiper-pagination',
                clickable: false,
                dynamicBullets: true,
                dynamicMainBullets: 15,
                renderBullet: (index, className) => {
                    if (index + 1 <= this.endPage) {
                        return `<span class="${className} bullet">${index + 1}</span>`;
                    } else {
                        return `<span class="${className} swiper-pagination-bullet-disabled">${index + 1}</span>`;
                    }
                }
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
        });
    }

    private onBulletClick(index: number): void {
        this.swiper.slideTo(index, 500, true);
    }

    private activateBullet(index: number): void {
        const bullets = document.getElementsByClassName('swiper-pagination-bullet');
        const bullet = bullets.item(index);
        bullet.classList.remove('swiper-pagination-bullet-disabled');
        bullet.classList.add('bullet');
        bullet.addEventListener('click', () => this.onBulletClick(index));
    }

    private hideFullscreen(): void {
        this.fullscreenContainer.style.display = 'none';
    }

    private unhideFullscreen(): void {
        this.fullscreenContainer.style.display = 'block';
    }

    private addWindowEventListener(window: Window) {
        window.addEventListener('keydown', e => {
            if (e.key === 'ArrowRight') {
                this.swiper.slideNext();
            } else if (e.key === 'ArrowLeft') {
                this.swiper.slidePrev();
            }
        });
    }

    private suspendDataCorrector(isCorrect: boolean): void {
        if (!isCorrect && this.videos.length > 0) {
            this.isSuspendDataPopulated = false;
            this.progressMeasure = 0;
            this.createSuspendData();
        }
    }
}
