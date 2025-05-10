package com.movtery.zalithlauncher.renderer.renderers

import com.movtery.zalithlauncher.renderer.RendererInterface
import com.movtery.zalithlauncher.utils.path.PathManager
import java.io.File

class VirGLRenderer : RendererInterface {
    override fun getRendererId(): String = "gallium_virgl"

    override fun getUniqueIdentifier(): String = "a3ccc1fe-de3f-4a81-8c45-2485181b63b3"

    override fun getRendererName(): String = "VirGLRenderer"

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy {
        mapOf(
            "VTEST_SOCKET_NAME" to File(PathManager.DIR_CACHE, ".virgl_test").absolutePath,
            "MESA_GL_VERSION_OVERRIDE" to "3.2",
            "MESA_GLSL_VERSION_OVERRIDE" to "320"
        )
    }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libOSMesa_2121.so"
}
