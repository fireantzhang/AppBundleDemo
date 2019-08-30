#!/bin/sh

set -e

function echoInfo() {
    echo "[GP_AAB 打包验证]$1"
}

googlePlayTempFile="$(pwd)/GooglePlayTemp"

if [ ! -d "$googlePlayTempFile" ]; then
    echoInfo "GooglePlayTemp 文件不存在，不需要删除"
else
    echoInfo "GooglePlayTemp 已存在，需要先删除"
    rm -rf $googlePlayTempFile
fi

echoInfo "$googlePlayTempFile"

echoInfo "开始处理打包 aab 文件..."

#运行自定义的 gradle 任务，打包 aab 文件，并且拷贝到文件夹 GooglePlayTemp 中
./gradlew clean bGPA

echoInfo "1. aab 包打包完成"

echoInfo "2. 获取 aab 包路径..."

allAabFiles="$(cd "$(pwd)/GooglePlayTemp" && ls | grep aab)"

echoInfo "   获取到 aab 包路径：$allAabFiles"

echoInfo "3. 获取设备的 spec.json 文件..."

# 获取连接设备的配置 json 文件，主要是为了避免生成比较全面的 apks 文件【时间会长很多】
device_spec_file="$(pwd)/GooglePlayTemp"/device-spec.json

bundletool get-device-spec --output=$device_spec_file --overwrite

echoInfo "   设备的 spec.json 文件获取成功: $device_spec_file"

echoInfo "4. 正在生成 apks 包..."

apks_file="$(pwd)/GooglePlayTemp"/app_bundle.apks

bundletool build-apks --bundle="$(pwd)/GooglePlayTemp"/$allAabFiles --device-spec=$device_spec_file --output=$apks_file --ks=./app/release.jks --ks-pass=pass:fireantzhang --ks-key-alias=fireantzhang --key-pass=pass:fireantzhang --overwrite

echoInfo "   已经生成 apks 包：$apks_file"

echoInfo "5. 正在安装 apks 包..."

bundletool install-apks --apks=$apks_file

echoInfo "   已经安装到手机"

echoInfo "6. 正在启动应用..."

adb shell am start -n "com.fireantzhang.aabdemo/com.fireantzhang.aabdemo.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

echoInfo "   应用已经启动..."