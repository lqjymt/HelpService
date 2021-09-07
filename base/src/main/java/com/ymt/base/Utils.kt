
import android.util.Log
import android.widget.Toast
import com.ymt.base.BaseApp

/**
 * 描述：
 *
 * @author CoderPig on 2018/04/12 12:14.
 */

fun shortToast(msg: String) = Toast.makeText(BaseApp.app, msg, Toast.LENGTH_SHORT).show()

fun longToast(msg: String) = Toast.makeText(BaseApp.app, msg, Toast.LENGTH_LONG).show()
fun le(msg: String) = Log.e("HelperService",msg)
fun ld(msg: String) = Log.e("HelperService",msg)