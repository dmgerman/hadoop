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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|JMap
specifier|public
class|class
name|JMap
extends|extends
name|JCompType
block|{
DECL|field|level
specifier|static
specifier|private
name|int
name|level
init|=
literal|0
decl_stmt|;
DECL|method|getLevel ()
specifier|static
specifier|private
name|String
name|getLevel
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|level
argument_list|)
return|;
block|}
DECL|method|incrLevel ()
specifier|static
specifier|private
name|void
name|incrLevel
parameter_list|()
block|{
name|level
operator|++
expr_stmt|;
block|}
DECL|method|decrLevel ()
specifier|static
specifier|private
name|void
name|decrLevel
parameter_list|()
block|{
name|level
operator|--
expr_stmt|;
block|}
DECL|method|getId (String id)
specifier|static
specifier|private
name|String
name|getId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|id
operator|+
name|getLevel
argument_list|()
return|;
block|}
DECL|field|keyType
specifier|private
name|JType
name|keyType
decl_stmt|;
DECL|field|valueType
specifier|private
name|JType
name|valueType
decl_stmt|;
DECL|class|JavaMap
class|class
name|JavaMap
extends|extends
name|JavaCompType
block|{
DECL|field|key
name|JType
operator|.
name|JavaType
name|key
decl_stmt|;
DECL|field|value
name|JType
operator|.
name|JavaType
name|value
decl_stmt|;
DECL|method|JavaMap (JType.JavaType key, JType.JavaType value)
name|JavaMap
parameter_list|(
name|JType
operator|.
name|JavaType
name|key
parameter_list|,
name|JType
operator|.
name|JavaType
name|value
parameter_list|)
block|{
name|super
argument_list|(
literal|"java.util.TreeMap<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|","
operator|+
name|value
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|">"
argument_list|,
literal|"Map"
argument_list|,
literal|"java.util.TreeMap<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|","
operator|+
name|value
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|">"
argument_list|,
literal|"TypeID.RIOType.MAP"
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new org.apache.hadoop.record.meta.MapTypeID("
operator|+
name|key
operator|.
name|getTypeIDObjectString
argument_list|()
operator|+
literal|", "
operator|+
name|value
operator|.
name|getTypeIDObjectString
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|method|genSetRTIFilter (CodeBuffer cb, Map<String, Integer> nestedStructMap)
name|void
name|genSetRTIFilter
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nestedStructMap
parameter_list|)
block|{
name|key
operator|.
name|genSetRTIFilter
argument_list|(
name|cb
argument_list|,
name|nestedStructMap
argument_list|)
expr_stmt|;
name|value
operator|.
name|genSetRTIFilter
argument_list|(
name|cb
argument_list|,
name|nestedStructMap
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
name|String
name|setType
init|=
literal|"java.util.Set<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|"> "
decl_stmt|;
name|String
name|iterType
init|=
literal|"java.util.Iterator<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|"> "
decl_stmt|;
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
name|setType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"set1"
argument_list|)
operator|+
literal|" = "
operator|+
name|fname
operator|+
literal|".keySet();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|setType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"set2"
argument_list|)
operator|+
literal|" = "
operator|+
name|other
operator|+
literal|".keySet();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|iterType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"miter1"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"set1"
argument_list|)
operator|+
literal|".iterator();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|iterType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"miter2"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"set2"
argument_list|)
operator|+
literal|".iterator();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for(; "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"miter1"
argument_list|)
operator|+
literal|".hasNext()&& "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"miter2"
argument_list|)
operator|+
literal|".hasNext();) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|key
operator|.
name|getType
argument_list|()
operator|+
literal|" "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k1"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"miter1"
argument_list|)
operator|+
literal|".next();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|key
operator|.
name|getType
argument_list|()
operator|+
literal|" "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k2"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"miter2"
argument_list|)
operator|+
literal|".next();\n"
argument_list|)
expr_stmt|;
name|key
operator|.
name|genCompareTo
argument_list|(
name|cb
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k1"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k2"
argument_list|)
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"if ("
operator|+
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"ret != 0) { return "
operator|+
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"ret; }\n"
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
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"ret = ("
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"set1"
argument_list|)
operator|+
literal|".size() - "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"set2"
argument_list|)
operator|+
literal|".size());\n"
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
DECL|method|genReadMethod (CodeBuffer cb, String fname, String tag, boolean decl)
name|void
name|genReadMethod
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|String
name|fname
parameter_list|,
name|String
name|tag
parameter_list|,
name|boolean
name|decl
parameter_list|)
block|{
if|if
condition|(
name|decl
condition|)
block|{
name|cb
operator|.
name|append
argument_list|(
name|getType
argument_list|()
operator|+
literal|" "
operator|+
name|fname
operator|+
literal|";\n"
argument_list|)
expr_stmt|;
block|}
name|cb
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
expr_stmt|;
name|incrLevel
argument_list|()
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"org.apache.hadoop.record.Index "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"midx"
argument_list|)
operator|+
literal|" = "
operator|+
name|Consts
operator|.
name|RECORD_INPUT
operator|+
literal|".startMap(\""
operator|+
name|tag
operator|+
literal|"\");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|fname
operator|+
literal|"=new "
operator|+
name|getType
argument_list|()
operator|+
literal|"();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for (; !"
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"midx"
argument_list|)
operator|+
literal|".done(); "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"midx"
argument_list|)
operator|+
literal|".incr()) {\n"
argument_list|)
expr_stmt|;
name|key
operator|.
name|genReadMethod
argument_list|(
name|cb
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|value
operator|.
name|genReadMethod
argument_list|(
name|cb
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"v"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"v"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|fname
operator|+
literal|".put("
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k"
argument_list|)
operator|+
literal|","
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"v"
argument_list|)
operator|+
literal|");\n"
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
name|Consts
operator|.
name|RECORD_INPUT
operator|+
literal|".endMap(\""
operator|+
name|tag
operator|+
literal|"\");\n"
argument_list|)
expr_stmt|;
name|decrLevel
argument_list|()
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|genWriteMethod (CodeBuffer cb, String fname, String tag)
name|void
name|genWriteMethod
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|,
name|String
name|fname
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|String
name|setType
init|=
literal|"java.util.Set<java.util.Map.Entry<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|","
operator|+
name|value
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|">> "
decl_stmt|;
name|String
name|entryType
init|=
literal|"java.util.Map.Entry<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|","
operator|+
name|value
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|"> "
decl_stmt|;
name|String
name|iterType
init|=
literal|"java.util.Iterator<java.util.Map.Entry<"
operator|+
name|key
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|","
operator|+
name|value
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|">> "
decl_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
expr_stmt|;
name|incrLevel
argument_list|()
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|Consts
operator|.
name|RECORD_OUTPUT
operator|+
literal|".startMap("
operator|+
name|fname
operator|+
literal|",\""
operator|+
name|tag
operator|+
literal|"\");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|setType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"es"
argument_list|)
operator|+
literal|" = "
operator|+
name|fname
operator|+
literal|".entrySet();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for("
operator|+
name|iterType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"midx"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"es"
argument_list|)
operator|+
literal|".iterator(); "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"midx"
argument_list|)
operator|+
literal|".hasNext();) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|entryType
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"me"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"midx"
argument_list|)
operator|+
literal|".next();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|key
operator|.
name|getType
argument_list|()
operator|+
literal|" "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"me"
argument_list|)
operator|+
literal|".getKey();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|value
operator|.
name|getType
argument_list|()
operator|+
literal|" "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"v"
argument_list|)
operator|+
literal|" = "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"me"
argument_list|)
operator|+
literal|".getValue();\n"
argument_list|)
expr_stmt|;
name|key
operator|.
name|genWriteMethod
argument_list|(
name|cb
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"k"
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|.
name|genWriteMethod
argument_list|(
name|cb
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"v"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"v"
argument_list|)
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
name|Consts
operator|.
name|RECORD_OUTPUT
operator|+
literal|".endMap("
operator|+
name|fname
operator|+
literal|",\""
operator|+
name|tag
operator|+
literal|"\");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|decrLevel
argument_list|()
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
name|incrLevel
argument_list|()
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int "
operator|+
name|getId
argument_list|(
literal|"mi"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.readVInt("
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
literal|"int "
operator|+
name|getId
argument_list|(
literal|"mz"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.getVIntSize("
operator|+
name|getId
argument_list|(
literal|"mi"
argument_list|)
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|s
operator|+
literal|"+="
operator|+
name|getId
argument_list|(
literal|"mz"
argument_list|)
operator|+
literal|"; "
operator|+
name|l
operator|+
literal|"-="
operator|+
name|getId
argument_list|(
literal|"mz"
argument_list|)
operator|+
literal|";\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for (int "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|" = 0; "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|"< "
operator|+
name|getId
argument_list|(
literal|"mi"
argument_list|)
operator|+
literal|"; "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|"++) {"
argument_list|)
expr_stmt|;
name|key
operator|.
name|genSlurpBytes
argument_list|(
name|cb
argument_list|,
name|b
argument_list|,
name|s
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|value
operator|.
name|genSlurpBytes
argument_list|(
name|cb
argument_list|,
name|b
argument_list|,
name|s
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|decrLevel
argument_list|()
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
name|incrLevel
argument_list|()
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int "
operator|+
name|getId
argument_list|(
literal|"mi1"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.readVInt(b1, s1);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int "
operator|+
name|getId
argument_list|(
literal|"mi2"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.readVInt(b2, s2);\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int "
operator|+
name|getId
argument_list|(
literal|"mz1"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.getVIntSize("
operator|+
name|getId
argument_list|(
literal|"mi1"
argument_list|)
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"int "
operator|+
name|getId
argument_list|(
literal|"mz2"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.getVIntSize("
operator|+
name|getId
argument_list|(
literal|"mi2"
argument_list|)
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"s1+="
operator|+
name|getId
argument_list|(
literal|"mz1"
argument_list|)
operator|+
literal|"; s2+="
operator|+
name|getId
argument_list|(
literal|"mz2"
argument_list|)
operator|+
literal|"; l1-="
operator|+
name|getId
argument_list|(
literal|"mz1"
argument_list|)
operator|+
literal|"; l2-="
operator|+
name|getId
argument_list|(
literal|"mz2"
argument_list|)
operator|+
literal|";\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for (int "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|" = 0; "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|"< "
operator|+
name|getId
argument_list|(
literal|"mi1"
argument_list|)
operator|+
literal|"&& "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|"< "
operator|+
name|getId
argument_list|(
literal|"mi2"
argument_list|)
operator|+
literal|"; "
operator|+
name|getId
argument_list|(
literal|"midx"
argument_list|)
operator|+
literal|"++) {"
argument_list|)
expr_stmt|;
name|key
operator|.
name|genCompareBytes
argument_list|(
name|cb
argument_list|)
expr_stmt|;
name|value
operator|.
name|genSlurpBytes
argument_list|(
name|cb
argument_list|,
literal|"b1"
argument_list|,
literal|"s1"
argument_list|,
literal|"l1"
argument_list|)
expr_stmt|;
name|value
operator|.
name|genSlurpBytes
argument_list|(
name|cb
argument_list|,
literal|"b2"
argument_list|,
literal|"s2"
argument_list|,
literal|"l2"
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
literal|"if ("
operator|+
name|getId
argument_list|(
literal|"mi1"
argument_list|)
operator|+
literal|" != "
operator|+
name|getId
argument_list|(
literal|"mi2"
argument_list|)
operator|+
literal|") { return ("
operator|+
name|getId
argument_list|(
literal|"mi1"
argument_list|)
operator|+
literal|"<"
operator|+
name|getId
argument_list|(
literal|"mi2"
argument_list|)
operator|+
literal|")?-1:0; }\n"
argument_list|)
expr_stmt|;
name|decrLevel
argument_list|()
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
DECL|class|CppMap
class|class
name|CppMap
extends|extends
name|CppCompType
block|{
DECL|field|key
name|JType
operator|.
name|CppType
name|key
decl_stmt|;
DECL|field|value
name|JType
operator|.
name|CppType
name|value
decl_stmt|;
DECL|method|CppMap (JType.CppType key, JType.CppType value)
name|CppMap
parameter_list|(
name|JType
operator|.
name|CppType
name|key
parameter_list|,
name|JType
operator|.
name|CppType
name|value
parameter_list|)
block|{
name|super
argument_list|(
literal|"::std::map< "
operator|+
name|key
operator|.
name|getType
argument_list|()
operator|+
literal|", "
operator|+
name|value
operator|.
name|getType
argument_list|()
operator|+
literal|">"
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::MapTypeID("
operator|+
name|key
operator|.
name|getTypeIDObjectString
argument_list|()
operator|+
literal|", "
operator|+
name|value
operator|.
name|getTypeIDObjectString
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|method|genSetRTIFilter (CodeBuffer cb)
name|void
name|genSetRTIFilter
parameter_list|(
name|CodeBuffer
name|cb
parameter_list|)
block|{
name|key
operator|.
name|genSetRTIFilter
argument_list|(
name|cb
argument_list|)
expr_stmt|;
name|value
operator|.
name|genSetRTIFilter
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Creates a new instance of JMap */
DECL|method|JMap (JType t1, JType t2)
specifier|public
name|JMap
parameter_list|(
name|JType
name|t1
parameter_list|,
name|JType
name|t2
parameter_list|)
block|{
name|setJavaType
argument_list|(
operator|new
name|JavaMap
argument_list|(
name|t1
operator|.
name|getJavaType
argument_list|()
argument_list|,
name|t2
operator|.
name|getJavaType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppMap
argument_list|(
name|t1
operator|.
name|getCppType
argument_list|()
argument_list|,
name|t2
operator|.
name|getCppType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setCType
argument_list|(
operator|new
name|CType
argument_list|()
argument_list|)
expr_stmt|;
name|keyType
operator|=
name|t1
expr_stmt|;
name|valueType
operator|=
name|t2
expr_stmt|;
block|}
DECL|method|getSignature ()
name|String
name|getSignature
parameter_list|()
block|{
return|return
literal|"{"
operator|+
name|keyType
operator|.
name|getSignature
argument_list|()
operator|+
name|valueType
operator|.
name|getSignature
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

