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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|JBoolean
specifier|public
class|class
name|JBoolean
extends|extends
name|JType
block|{
DECL|class|JavaBoolean
class|class
name|JavaBoolean
extends|extends
name|JType
operator|.
name|JavaType
block|{
DECL|method|JavaBoolean ()
name|JavaBoolean
parameter_list|()
block|{
name|super
argument_list|(
literal|"boolean"
argument_list|,
literal|"Bool"
argument_list|,
literal|"Boolean"
argument_list|,
literal|"TypeID.RIOType.BOOL"
argument_list|)
expr_stmt|;
block|}
DECL|method|genCompareTo (CodeBuffer cb, String fname, String other)
name|void
name|genCompareTo
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|String
name|fname
parameter_list|,
name|String
name|other
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
literal|"ret = ("
operator|+
name|fname
operator|+
literal|" == "
operator|+
name|other
operator|+
literal|")? 0 : ("
operator|+
name|fname
operator|+
literal|"?1:-1);\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"org.apache.hadoop.record.meta.TypeID.BoolTypeID"
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
literal|"ret = ("
operator|+
name|fname
operator|+
literal|")?0:1;\n"
argument_list|)
expr_stmt|;
block|}
comment|// In Binary format, boolean is written as byte. true = 1, false = 0
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
literal|"<1) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"throw new java.io.IOException(\"Boolean is exactly 1 byte."
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
literal|"++; "
operator|+
name|l
operator|+
literal|"--;\n"
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
comment|// In Binary format, boolean is written as byte. true = 1, false = 0
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
literal|"if (l1<1 || l2<1) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"throw new java.io.IOException(\"Boolean is exactly 1 byte."
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
literal|"if (b1[s1] != b2[s2]) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"return (b1[s1]<b2[s2])? -1 : 0;\n"
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
literal|"s1++; s2++; l1--; l2--;\n"
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
DECL|class|CppBoolean
class|class
name|CppBoolean
extends|extends
name|CppType
block|{
DECL|method|CppBoolean ()
name|CppBoolean
parameter_list|()
block|{
name|super
argument_list|(
literal|"bool"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::TypeID(::hadoop::RIOTYPE_BOOL)"
return|;
block|}
block|}
comment|/** Creates a new instance of JBoolean */
DECL|method|JBoolean ()
specifier|public
name|JBoolean
parameter_list|()
block|{
name|setJavaType
argument_list|(
operator|new
name|JavaBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppBoolean
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
literal|"z"
return|;
block|}
block|}
end_class

end_unit

