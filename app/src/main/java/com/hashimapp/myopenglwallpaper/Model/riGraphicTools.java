package com.hashimapp.myopenglwallpaper.Model;

import android.opengl.GLES20;

public class riGraphicTools
{

    // Program variables
    public static int sp_SolidColor;
    public static int sp_Image;
    public static int sp_Color;
    public static int sp_Particle;


    /* SHADER Solid
     *
     * This shader is for rendering a colored primitive.
     *
     */
    public static final String vs_SolidColor =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  _vColor = vColor;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String fs_SolidColor =
            "precision mediump float;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  gl_FragColor = _vColor;" +
                    "}";

    /* SHADER Image
     *
     * This shader is for rendering 2D images straight from a texture
     * No additional effects.
     *
     */
    public static final String vs_Image =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_Color;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec4 v_Color;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "  v_Color = a_Color;" +
                    "}";

    public static final String fs_Image =
                    "varying vec2 v_texCoord;" +
                    "varying vec4 v_Color;" +
                    "uniform sampler2D s_texture;" +
                    "uniform float bias;" +
                    "uniform float alpha;" +

                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, v_texCoord, bias ) * v_Color *alpha;" +
                    "}";


    public static final String vs_Particle =
                    "uniform float u_time;                                \n" +
                    "uniform vec3 u_centerPosition;                       \n" +
                    "attribute float a_lifetime;                          \n" +
                    "attribute vec3 a_startPosition;                      \n" +
                    "attribute vec3 a_endPosition;                        \n" +
                    "varying float v_lifetime;                            \n" +
                    "void main()                                          \n" +
                    "{                                                    \n" +
                    "  if ( u_time <= a_lifetime )                        \n" +
                    "  {                                                  \n" +
                    "    gl_Position.xyz = a_startPosition +              \n" +
                    "                      (u_time * a_endPosition);      \n" +
                    "    gl_Position.xyz += u_centerPosition;             \n" +
                    "    gl_Position.w = 1.0;                             \n" +
                    "  }                                                  \n" +
                    "  else                                               \n" +
                    "     gl_Position = vec4( -1000, -1000, 0, 0 );       \n" +
                    "  v_lifetime = 1.0 - ( u_time / a_lifetime );        \n" +
                    "  v_lifetime = clamp ( v_lifetime, 0.0, 1.0 );       \n" +
                    "  gl_PointSize = ( v_lifetime * v_lifetime ) * 100.0; \n" +
                    "}";


    public static final String fs_Particle =
            "precision mediump float;                             		  \n" +
                    "uniform vec4 u_color;                                \n" +
                    "varying float v_lifetime;                            \n" +
                    "uniform sampler2D s_texture;                         \n" +
                    "void main()                                          \n" +
                    "{                                                    \n" +
                    "  vec4 texColor;                                     \n" +
                    "  texColor = texture2D( s_texture, gl_PointCoord );  \n" +
                    "  gl_FragColor = vec4( u_color ) * texColor;         \n" +
                    "  gl_FragColor.a *= v_lifetime;                      \n" +
                    "}                                                    \n";

    public static final String vs_Color =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_Color;" +
                    "varying vec4 v_Color;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_Color = a_Color;" +
                    "}";

    public static final String fs_Color =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color;" +
                    "  gl_FragColor.rgb *= v_Color.a;" +
                    "}";


    public static int loadShader(int type, String shaderCode)
    {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // return the shader
        return shader;
    }
}
