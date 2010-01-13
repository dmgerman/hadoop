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
comment|/**  * Code generator for "buffer" type.  *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|JBuffer
specifier|public
class|class
name|JBuffer
extends|extends
name|JCompType
block|{
DECL|class|JavaBuffer
class|class
name|JavaBuffer
extends|extends
name|JavaCompType
block|{
DECL|method|JavaBuffer ()
name|JavaBuffer
parameter_list|()
block|{
name|super
argument_list|(
literal|"org.apache.hadoop.record.Buffer"
argument_list|,
literal|"Buffer"
argument_list|,
literal|"org.apache.hadoop.record.Buffer"
argument_list|,
literal|"TypeID.RIOType.BUFFER"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"org.apache.hadoop.record.meta.TypeID.BufferTypeID"
return|;
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
literal|"ret = "
operator|+
name|fname
operator|+
literal|".compareTo("
operator|+
name|other
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|genEquals (CodeBuffer cb, String fname, String peer)
name|void
name|genEquals
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|String
name|fname
parameter_list|,
name|String
name|peer
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
literal|"ret = "
operator|+
name|fname
operator|+
literal|".equals("
operator|+
name|peer
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
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
literal|"ret = "
operator|+
name|fname
operator|+
literal|".hashCode();\n"
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
literal|" += z+i; "
operator|+
name|l
operator|+
literal|" -= (z+i);\n"
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
block|}
DECL|class|CppBuffer
class|class
name|CppBuffer
extends|extends
name|CppCompType
block|{
DECL|method|CppBuffer ()
name|CppBuffer
parameter_list|()
block|{
name|super
argument_list|(
literal|" ::std::string"
argument_list|)
expr_stmt|;
block|}
DECL|method|genGetSet (CodeBuffer cb, String fname)
name|void
name|genGetSet
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
literal|"virtual const "
operator|+
name|getType
argument_list|()
operator|+
literal|"& get"
operator|+
name|toCamelCase
argument_list|(
name|fname
argument_list|)
operator|+
literal|"() const {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"return "
operator|+
name|fname
operator|+
literal|";\n"
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
literal|"virtual "
operator|+
name|getType
argument_list|()
operator|+
literal|"& get"
operator|+
name|toCamelCase
argument_list|(
name|fname
argument_list|)
operator|+
literal|"() {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"return "
operator|+
name|fname
operator|+
literal|";\n"
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
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::TypeID(::hadoop::RIOTYPE_BUFFER)"
return|;
block|}
block|}
comment|/** Creates a new instance of JBuffer */
DECL|method|JBuffer ()
specifier|public
name|JBuffer
parameter_list|()
block|{
name|setJavaType
argument_list|(
operator|new
name|JavaBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppBuffer
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
literal|"B"
return|;
block|}
block|}
end_class

end_unit

