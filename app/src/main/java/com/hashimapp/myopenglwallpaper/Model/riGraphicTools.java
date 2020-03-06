package com.hashimapp.myopenglwallpaper.Model;

import android.opengl.GLES20;

public class riGraphicTools
{

    // Program variables
    public static int sp_SolidColor;
    public static int sp_Image;
    public static int sp_Color;
    public static int sp_Particle;

    public static int fsImageID;
    public static int vsImageID;
    public static int fsParticleID;
    public static int vsParticleID;


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
                    "attribute vec2 vOffset;" +
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
                    "  gl_FragColor = texture2D( s_texture, v_texCoord, bias ) * v_Color * alpha;" +
                    "}";


    public static final String vs_Particle =
            "uniform mat4 uMVPMatrix;" +
            "precision mediump float;" +
            "attribute vec4 a_Position;" +
            "attribute vec4 a_move;" +
            "uniform float a_time;" +
            "uniform float a_stufff;"+
            "attribute vec4 a_color;" +
            "varying vec4 v_color;" +
            "attribute float a_life;" +
            "attribute float a_age;" +
            "varying float alpha;" +
            "float time;" +
            "attribute vec2 TextureCoordIn;"+
            "varying vec2 TextureCoord;" +
            "varying vec2 TextureSize;" +

            "void main()                                          \n" +
            "{                                                    \n" +
                    "alpha = a_life - (a_time * 10.0 * a_age);" +
                    "time = a_time;" +

                    "if (alpha < 0.0)" +
                    "{" +
                         "float td = a_life/a_age;" +
                         "td /= 10.0;" +
                         "float df = a_time/td;" +
                         "int div = int(df);" +
                         "df = float(div);" +
                         "td *= df;" +
                         "time = a_time - td;" +
                         "alpha = a_life - (time * 10.0 * a_age);" +
                    "}" +
//                    "gl_Position = uMVPMatrix * vec4(a_Position.xyz, 1.0) ;              \n" +
                    "gl_Position = uMVPMatrix * a_Position ;              \n" +
                    "gl_Position += (time * a_move);" +
                    "gl_Position.w = 1.0;" +
                    "TextureCoord = TextureCoordIn;" +
                    "TextureSize = vec2(a_stufff, a_stufff);" +
                    "gl_PointSize = 100.0;" +

//                    "  if ( u_time <= a_lifetime )                        \n" +
//                    "  {                                                  \n" +
////                    "                      (u_time * a_endPosition);      \n" +
//                    "    gl_Position.xyz += u_centerPosition;             \n" +
//                    "    gl_Position.w = 1.0;                             \n" +
//                    "  }                                                  \n" +
//                    "  else                                               \n" +
//                    "     gl_Position = vec4( -1000, -1000, 0, 0 );       \n" +
//                    "  v_lifetime = 1.0 - ( u_time / a_lifetime );        \n" +
//                    "  v_lifetime = clamp ( v_lifetime, 0.0, 1.0 );       \n" +
//                    "  gl_PointSize = ( v_lifetime * v_lifetime ) * 100.0; \n" +
                    "}";



    public static final String fs_Particle =
            "precision mediump float;                             		        \n" +
                    "uniform vec4 u_color;                                      \n" +
                    "varying float v_lifetime;                                  \n" +
                    "uniform sampler2D s_texture;                               \n" +
                    "varying mediump vec2 TextureCoord;                           " +
                    "varying mediump vec2 TextureSize;                            " +

                    "void main()                                                \n" +
                    "{                                                          \n" +
                    "  mediump vec2 realTexCoord = TextureCoord + (gl_PointCoord * TextureSize);" +
                    "  mediump vec4 texColor = texture2D( s_texture, realTexCoord, -1.0);   \n" +
                    "    if(texColor.a == 0.0){\n" +
                    "        discard;\n" +
                    "    }"+
                    "  gl_FragColor = texColor;                                 \n" +
//                    "  gl_FragColor.w *= alpha;                            \n" +
                    "}                                                          \n";

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
