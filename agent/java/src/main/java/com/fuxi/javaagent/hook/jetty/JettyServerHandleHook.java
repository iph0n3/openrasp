/*
 * Copyright 2017-2018 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fuxi.javaagent.hook.jetty;

import com.fuxi.javaagent.hook.AbstractClassHook;
import com.fuxi.javaagent.hook.catalina.ApplicationFilterHook;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Created by tyy on 9/22/17.
 *
 * jetty请求的hook点
 */
public class JettyServerHandleHook extends AbstractClassHook {

    @Override
    public boolean isClassMatched(String className) {
        return className.equals("org/eclipse/jetty/server/handler/HandlerWrapper");
    }

    @Override
    public String getType() {
        return "request";
    }

    @Override
    protected MethodVisitor hookMethod(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
        if (name.equals("handle")) {
            return new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
                @Override
                protected void onMethodEnter() {
                    loadThis();
                    loadArg(2);
                    loadArg(3);
                    invokeStatic(Type.getType(ApplicationFilterHook.class),
                            new Method("checkRequest", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"));
                }
            };
        }
        return mv;
    }
}
