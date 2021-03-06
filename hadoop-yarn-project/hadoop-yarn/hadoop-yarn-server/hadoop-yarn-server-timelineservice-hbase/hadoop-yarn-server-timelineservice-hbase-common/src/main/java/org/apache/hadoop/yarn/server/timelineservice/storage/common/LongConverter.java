begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|hbase
operator|.
name|util
operator|.
name|Bytes
import|;
end_import

begin_comment
comment|/**  * Encodes a value by interpreting it as a Long and converting it to bytes and  * decodes a set of bytes as a Long.  */
end_comment

begin_class
DECL|class|LongConverter
specifier|public
specifier|final
class|class
name|LongConverter
implements|implements
name|NumericValueConverter
implements|,
name|Serializable
block|{
comment|/**    * Added because we implement Comparator<Number>.    */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|LongConverter ()
specifier|public
name|LongConverter
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|encodeValue (Object value)
specifier|public
name|byte
index|[]
name|encodeValue
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|HBaseTimelineSchemaUtils
operator|.
name|isIntegralValue
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expected integral value"
argument_list|)
throw|;
block|}
return|return
name|Bytes
operator|.
name|toBytes
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|decodeValue (byte[] bytes)
specifier|public
name|Object
name|decodeValue
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Bytes
operator|.
name|toLong
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|/**    * Compares two numbers as longs. If either number is null, it will be taken    * as 0.    *    * @param num1 the first {@code Long} to compare.    * @param num2 the second {@code Long} to compare.    * @return -1 if num1 is less than num2, 0 if num1 is equal to num2 and 1 if    * num1 is greater than num2.    */
annotation|@
name|Override
DECL|method|compare (Number num1, Number num2)
specifier|public
name|int
name|compare
parameter_list|(
name|Number
name|num1
parameter_list|,
name|Number
name|num2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
operator|(
name|num1
operator|==
literal|null
operator|)
condition|?
literal|0L
else|:
name|num1
operator|.
name|longValue
argument_list|()
argument_list|,
operator|(
name|num2
operator|==
literal|null
operator|)
condition|?
literal|0L
else|:
name|num2
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add (Number num1, Number num2, Number...numbers)
specifier|public
name|Number
name|add
parameter_list|(
name|Number
name|num1
parameter_list|,
name|Number
name|num2
parameter_list|,
name|Number
modifier|...
name|numbers
parameter_list|)
block|{
name|long
name|sum
init|=
operator|(
operator|(
name|num1
operator|==
literal|null
operator|)
condition|?
literal|0L
else|:
name|num1
operator|.
name|longValue
argument_list|()
operator|)
operator|+
operator|(
operator|(
name|num2
operator|==
literal|null
operator|)
condition|?
literal|0L
else|:
name|num2
operator|.
name|longValue
argument_list|()
operator|)
decl_stmt|;
for|for
control|(
name|Number
name|num
range|:
name|numbers
control|)
block|{
name|sum
operator|=
name|sum
operator|+
operator|(
operator|(
name|num
operator|==
literal|null
operator|)
condition|?
literal|0L
else|:
name|num
operator|.
name|longValue
argument_list|()
operator|)
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
comment|/**    * Converts a timestamp into it's inverse timestamp to be used in (row) keys    * where we want to have the most recent timestamp in the top of the table    * (scans start at the most recent timestamp first).    *    * @param key value to be inverted so that the latest version will be first in    *          a scan.    * @return inverted long    */
DECL|method|invertLong (long key)
specifier|public
specifier|static
name|long
name|invertLong
parameter_list|(
name|long
name|key
parameter_list|)
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
operator|-
name|key
return|;
block|}
block|}
end_class

end_unit

