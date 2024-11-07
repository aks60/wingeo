package common;

import org.mozilla.javascript.Context;
//import java.util.function.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

//import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
//import org.mozilla.javascript.Scriptable;
//import org.mozilla.javascript.ScriptableObject;

public class Rhino {
 
    
        public static void test() {
        Context cx = Context.enter();
        try {
            // Set version to JavaScript1.2 so that we get object-literal style
            // printing instead of "[object Object]"
            cx.setLanguageVersion(Context.VERSION_1_2);

            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed.
            Scriptable scope = cx.initStandardObjects();

            // Now we can evaluate a script. Let's create a new object
            // using the object literal notation.
            Object result = cx.evaluateString(scope, "obj = {a:1, b:['x','y']}", "MySource", 1, null);

            Scriptable obj = (Scriptable) scope.get("obj", scope);

            // Should print "obj == result" (Since the result of an assignment
            // expression is the value that was assigned)
            System.out.println("obj " + (obj == result ? "==" : "!=") + " result");

            // Should print "obj.a == 1"
            System.out.println("obj.a == " + obj.get("a", obj));

            Scriptable b = (Scriptable) obj.get("b", obj);

            // Should print "obj.b[0] == x"
            System.out.println("obj.b[0] == " + b.get(0, b));

            // Should print "obj.b[1] == y"
            System.out.println("obj.b[1] == " + b.get(1, b));

            // Should print {a:1, b:["x", "y"]}
            Function fn = (Function) ScriptableObject.getProperty(obj, "toString");
            System.out.println(fn.call(cx, scope, obj, new Object[0]));
        } finally {
            Context.exit();
        }
    }
}
