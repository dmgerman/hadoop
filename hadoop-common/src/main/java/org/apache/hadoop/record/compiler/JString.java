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
DECL|class|JString
specifier|public
class|class
name|JString
extends|extends
name|JCompType
block|{
DECL|class|JavaString
class|class
name|JavaString
extends|extends
name|JavaCompType
block|{
DECL|method|JavaString ()
name|JavaString
parameter_list|()
block|{
name|super
argument_list|(
literal|"String"
argument_list|,
literal|"String"
argument_list|,
literal|"String"
argument_list|,
literal|"TypeID.RIOType.STRING"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"org.apache.hadoop.record.meta.TypeID.StringTypeID"
return|;
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
literal|"int i = org.apache.hadoop.record.Utils.readVInt("
operator|+
name|b
operator|+
literal|", "
operator|+
name|s
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int z = org.apache.hadoop.record.Utils.getVIntSize(i);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|s
operator|+
literal|"+=(z+i); "
operator|+
name|l
operator|+
literal|"-= (z+i);\n"
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
literal|"int i1 = org.apache.hadoop.record.Utils.readVInt(b1, s1);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int i2 = org.apache.hadoop.record.Utils.readVInt(b2, s2);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int z1 = org.apache.hadoop.record.Utils.getVIntSize(i1);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int z2 = org.apache.hadoop.record.Utils.getVIntSize(i2);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"s1+=z1; s2+=z2; l1-=z1; l2-=z2;\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int r1 = org.apache.hadoop.record.Utils.compareBytes(b1,s1,i1,b2,s2,i2);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"if (r1 != 0) { return (r1<0)?-1:0; }\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"s1+=i1; s2+=i2; l1-=i1; l1-=i2;\n"
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
DECL|method|genClone (CodeBuffer cb, String fname)
name|void
name|genClone
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
literal|"other."
operator|+
name|fname
operator|+
literal|" = this."
operator|+
name|fname
operator|+
literal|";\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CppString
class|class
name|CppString
extends|extends
name|CppCompType
block|{
DECL|method|CppString ()
name|CppString
parameter_list|()
block|{
name|super
argument_list|(
literal|"::std::string"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::TypeID(::hadoop::RIOTYPE_STRING)"
return|;
block|}
block|}
comment|/** Creates a new instance of JString */
DECL|method|JString ()
specifier|public
name|JString
parameter_list|()
block|{
name|setJavaType
argument_list|(
operator|new
name|JavaString
argument_list|()
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppString
argument_list|()
argument_list|)
expr_stmt|;
name|setCType
argument_list|(
operator|new
name|CCompType
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
literal|"s"
return|;
block|}
block|}
end_class

end_unit

