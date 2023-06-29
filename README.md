# modular
组件化框架：LiveData、ViewModel、RepoSitory、Dagger2、ARouter、Room 、Glide（后期整理好后会加入TCP模块、串口模块、CMake模块、PPT视频直播通信、Push模块、zxing模块、bluetooth模块）

依赖用法

maven {
            // Replace GITHUB_USERID with your personal or organisation user ID and
            // REPOSITORY with the name of the repository on GitHub
            url = uri("https://maven.pkg.github.com/lipeiyong/modular")
            //这部分代码不能提交到github.提交的话会导致token被删除
            credentials {
                username = 'visitor'
                password = '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'
            }
        }

    implementation 'com.lpy.modular:common:1.0.1'
