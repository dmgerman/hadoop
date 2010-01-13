begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record.compiler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|compiler
package|;
end_package

begin_comment
comment|/**  * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|JFloat
specifier|public
class|class
name|JFloat
extends|extends
name|JType
block|{
DECL|class|JavaFloat
class|class
name|JavaFloat
extends|extends
name|JavaType
block|{
DECL|method|JavaFloat ()
name|JavaFloat
parameter_list|()
block|{
name|super
argument_list|(
literal|"float"
argument_list|,
literal|"Float"
argument_list|,
literal|"Float"
argument_list|,
literal|"TypeID.RIOType.FLOAT"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"org.apache.hadoop.record.meta.TypeID.FloatTypeID"
return|;
block|}
DECL|method|genHashCode (CodeBuffer cb, String fname)
name|void
name|genHashCode
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|String
name|fname
parameter_list|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"ret = Float.floatToIntBits("
operator|+
name|fname
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|genSlurpBytes (CodeBuffer cb, String b, String s, String l)
name|void
name|genSlurpBytes
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|String
name|b
parameter_list|,
name|String
name|s
parameter_list|,
name|String
name|l
parameter_list|)
block|{
name|cb
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"if ("
operator|+
name|l
operator|+
literal|"<4) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"throw new java.io.IOException(\"Float is exactly 4 bytes."
operator|+
literal|" Provided buffer is smaller.\");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|s
operator|+
literal|"+=4; "
operator|+
name|l
operator|+
literal|"-=4;\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|genCompareBytes (CodeBuffer cb)
name|void
name|genCompareBytes
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|)
block|{
name|cb
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"if (l1<4 || l2<4) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"throw new java.io.IOException(\"Float is exactly 4 bytes."
operator|+
literal|" Provided buffer is smaller.\");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"float f1 = org.apache.hadoop.record.Utils.readFloat(b1, s1);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"float f2 = org.apache.hadoop.record.Utils.readFloat(b2, s2);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"if (f1 != f2) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"return ((f1-f2)< 0) ? -1 : 0;\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"s1+=4; s2+=4; l1-=4; l2-=4;\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CppFloat
class|class
name|CppFloat
extends|extends
name|CppType
block|{
DECL|method|CppFloat ()
name|CppFloat
parameter_list|()
block|{
name|super
argument_list|(
literal|"float"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::TypeID(::hadoop::RIOTYPE_FLOAT)"
return|;
block|}
block|}
comment|/** Creates a new instance of JFloat */
DECL|method|JFloat ()
specifier|public
name|JFloat
parameter_list|()
block|{
name|setJavaType
argument_list|(
operator|new
name|JavaFloat
argument_list|()
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppFloat
argument_list|()
argument_list|)
expr_stmt|;
name|setCType
argument_list|(
operator|new
name|CType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSignature ()
name|String
name|getSignature
parameter_list|()
block|{
return|return
literal|"f"
return|;
block|}
block|}
end_class

end_unit

