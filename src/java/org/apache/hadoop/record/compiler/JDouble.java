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
comment|/**  */
end_comment

begin_class
DECL|class|JDouble
specifier|public
class|class
name|JDouble
extends|extends
name|JType
block|{
DECL|class|JavaDouble
class|class
name|JavaDouble
extends|extends
name|JavaType
block|{
DECL|method|JavaDouble ()
name|JavaDouble
parameter_list|()
block|{
name|super
argument_list|(
literal|"double"
argument_list|,
literal|"Double"
argument_list|,
literal|"Double"
argument_list|,
literal|"TypeID.RIOType.DOUBLE"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"org.apache.hadoop.record.meta.TypeID.DoubleTypeID"
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
name|String
name|tmp
init|=
literal|"Double.doubleToLongBits("
operator|+
name|fname
operator|+
literal|")"
decl_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"ret = (int)("
operator|+
name|tmp
operator|+
literal|"^("
operator|+
name|tmp
operator|+
literal|">>>32));\n"
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
literal|"<8) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"throw new java.io.IOException(\"Double is exactly 8 bytes."
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
literal|"+=8; "
operator|+
name|l
operator|+
literal|"-=8;\n"
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
literal|"if (l1<8 || l2<8) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"throw new java.io.IOException(\"Double is exactly 8 bytes."
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
literal|"double d1 = org.apache.hadoop.record.Utils.readDouble(b1, s1);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"double d2 = org.apache.hadoop.record.Utils.readDouble(b2, s2);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"if (d1 != d2) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"return ((d1-d2)< 0) ? -1 : 0;\n"
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
literal|"s1+=8; s2+=8; l1-=8; l2-=8;\n"
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
DECL|class|CppDouble
class|class
name|CppDouble
extends|extends
name|CppType
block|{
DECL|method|CppDouble ()
name|CppDouble
parameter_list|()
block|{
name|super
argument_list|(
literal|"double"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::TypeID(::hadoop::RIOTYPE_DOUBLE)"
return|;
block|}
block|}
comment|/** Creates a new instance of JDouble */
DECL|method|JDouble ()
specifier|public
name|JDouble
parameter_list|()
block|{
name|setJavaType
argument_list|(
operator|new
name|JavaDouble
argument_list|()
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppDouble
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
literal|"d"
return|;
block|}
block|}
end_class

end_unit

