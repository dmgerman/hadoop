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
comment|/**  */
end_comment

begin_class
DECL|class|JVector
specifier|public
class|class
name|JVector
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
DECL|field|type
specifier|private
name|JType
name|type
decl_stmt|;
DECL|class|JavaVector
class|class
name|JavaVector
extends|extends
name|JavaCompType
block|{
DECL|field|element
specifier|private
name|JType
operator|.
name|JavaType
name|element
decl_stmt|;
DECL|method|JavaVector (JType.JavaType t)
name|JavaVector
parameter_list|(
name|JType
operator|.
name|JavaType
name|t
parameter_list|)
block|{
name|super
argument_list|(
literal|"java.util.ArrayList<"
operator|+
name|t
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|">"
argument_list|,
literal|"Vector"
argument_list|,
literal|"java.util.ArrayList<"
operator|+
name|t
operator|.
name|getWrapperType
argument_list|()
operator|+
literal|">"
argument_list|,
literal|"TypeID.RIOType.VECTOR"
argument_list|)
expr_stmt|;
name|element
operator|=
name|t
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new org.apache.hadoop.record.meta.VectorTypeID("
operator|+
name|element
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
name|element
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
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len1"
argument_list|)
operator|+
literal|" = "
operator|+
name|fname
operator|+
literal|".size();\n"
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
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len2"
argument_list|)
operator|+
literal|" = "
operator|+
name|other
operator|+
literal|".size();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for(int "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|" = 0; "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|"<"
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len1"
argument_list|)
operator|+
literal|"&& "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|"<"
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len2"
argument_list|)
operator|+
literal|"; "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|"++) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|element
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
literal|"e1"
argument_list|)
operator|+
literal|" = "
operator|+
name|fname
operator|+
literal|".get("
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|element
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
literal|"e2"
argument_list|)
operator|+
literal|" = "
operator|+
name|other
operator|+
literal|".get("
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|element
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
literal|"e1"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"e2"
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
literal|"len1"
argument_list|)
operator|+
literal|" - "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len2"
argument_list|)
operator|+
literal|");\n"
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
literal|"vidx"
argument_list|)
operator|+
literal|" = "
operator|+
name|Consts
operator|.
name|RECORD_INPUT
operator|+
literal|".startVector(\""
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
literal|"vidx"
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
literal|"vidx"
argument_list|)
operator|+
literal|".incr()) {\n"
argument_list|)
expr_stmt|;
name|element
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
literal|"e"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"e"
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
literal|".add("
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"e"
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
literal|".endVector(\""
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
literal|".startVector("
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
literal|"int "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len"
argument_list|)
operator|+
literal|" = "
operator|+
name|fname
operator|+
literal|".size();\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
literal|"for(int "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|" = 0; "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|"<"
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"len"
argument_list|)
operator|+
literal|"; "
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|"++) {\n"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|append
argument_list|(
name|element
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
literal|"e"
argument_list|)
operator|+
literal|" = "
operator|+
name|fname
operator|+
literal|".get("
operator|+
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"vidx"
argument_list|)
operator|+
literal|");\n"
argument_list|)
expr_stmt|;
name|element
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
literal|"e"
argument_list|)
argument_list|,
name|getId
argument_list|(
name|Consts
operator|.
name|RIO_PREFIX
operator|+
literal|"e"
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
literal|".endVector("
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
literal|"vi"
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
literal|"vz"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.getVIntSize("
operator|+
name|getId
argument_list|(
literal|"vi"
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
literal|"vz"
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
literal|"vz"
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
literal|"vidx"
argument_list|)
operator|+
literal|" = 0; "
operator|+
name|getId
argument_list|(
literal|"vidx"
argument_list|)
operator|+
literal|"< "
operator|+
name|getId
argument_list|(
literal|"vi"
argument_list|)
operator|+
literal|"; "
operator|+
name|getId
argument_list|(
literal|"vidx"
argument_list|)
operator|+
literal|"++)"
argument_list|)
expr_stmt|;
name|element
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
literal|"vi1"
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
literal|"vi2"
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
literal|"vz1"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.getVIntSize("
operator|+
name|getId
argument_list|(
literal|"vi1"
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
literal|"vz2"
argument_list|)
operator|+
literal|" = org.apache.hadoop.record.Utils.getVIntSize("
operator|+
name|getId
argument_list|(
literal|"vi2"
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
literal|"vz1"
argument_list|)
operator|+
literal|"; s2+="
operator|+
name|getId
argument_list|(
literal|"vz2"
argument_list|)
operator|+
literal|"; l1-="
operator|+
name|getId
argument_list|(
literal|"vz1"
argument_list|)
operator|+
literal|"; l2-="
operator|+
name|getId
argument_list|(
literal|"vz2"
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
literal|"vidx"
argument_list|)
operator|+
literal|" = 0; "
operator|+
name|getId
argument_list|(
literal|"vidx"
argument_list|)
operator|+
literal|"< "
operator|+
name|getId
argument_list|(
literal|"vi1"
argument_list|)
operator|+
literal|"&& "
operator|+
name|getId
argument_list|(
literal|"vidx"
argument_list|)
operator|+
literal|"< "
operator|+
name|getId
argument_list|(
literal|"vi2"
argument_list|)
operator|+
literal|"; "
operator|+
name|getId
argument_list|(
literal|"vidx"
argument_list|)
operator|+
literal|"++)"
argument_list|)
expr_stmt|;
name|element
operator|.
name|genCompareBytes
argument_list|(
name|cb
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
literal|"vi1"
argument_list|)
operator|+
literal|" != "
operator|+
name|getId
argument_list|(
literal|"vi2"
argument_list|)
operator|+
literal|") { return ("
operator|+
name|getId
argument_list|(
literal|"vi1"
argument_list|)
operator|+
literal|"<"
operator|+
name|getId
argument_list|(
literal|"vi2"
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
DECL|class|CppVector
class|class
name|CppVector
extends|extends
name|CppCompType
block|{
DECL|field|element
specifier|private
name|JType
operator|.
name|CppType
name|element
decl_stmt|;
DECL|method|CppVector (JType.CppType t)
name|CppVector
parameter_list|(
name|JType
operator|.
name|CppType
name|t
parameter_list|)
block|{
name|super
argument_list|(
literal|"::std::vector< "
operator|+
name|t
operator|.
name|getType
argument_list|()
operator|+
literal|">"
argument_list|)
expr_stmt|;
name|element
operator|=
name|t
expr_stmt|;
block|}
DECL|method|getTypeIDObjectString ()
name|String
name|getTypeIDObjectString
parameter_list|()
block|{
return|return
literal|"new ::hadoop::VectorTypeID("
operator|+
name|element
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
name|element
operator|.
name|genSetRTIFilter
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Creates a new instance of JVector */
DECL|method|JVector (JType t)
specifier|public
name|JVector
parameter_list|(
name|JType
name|t
parameter_list|)
block|{
name|type
operator|=
name|t
expr_stmt|;
name|setJavaType
argument_list|(
operator|new
name|JavaVector
argument_list|(
name|t
operator|.
name|getJavaType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setCppType
argument_list|(
operator|new
name|CppVector
argument_list|(
name|t
operator|.
name|getCppType
argument_list|()
argument_list|)
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
literal|"["
operator|+
name|type
operator|.
name|getSignature
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

