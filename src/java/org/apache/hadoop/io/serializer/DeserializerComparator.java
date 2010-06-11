begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.serializer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|serializer
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
name|util
operator|.
name|Comparator
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|InputBuffer
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
name|io
operator|.
name|RawComparator
import|;
end_import

begin_comment
comment|/**  *<p>  * A {@link RawComparator} that uses a {@link Deserializer} to deserialize  * the objects to be compared so that the standard {@link Comparator} can  * be used to compare them.  *</p>  *<p>  * One may optimize compare-intensive operations by using a custom  * implementation of {@link RawComparator} that operates directly  * on byte representations.  *</p>  * @param<T>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DeserializerComparator
specifier|public
specifier|abstract
class|class
name|DeserializerComparator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|RawComparator
argument_list|<
name|T
argument_list|>
block|{
DECL|field|buffer
specifier|private
name|InputBuffer
name|buffer
init|=
operator|new
name|InputBuffer
argument_list|()
decl_stmt|;
DECL|field|deserializer
specifier|private
name|Deserializer
argument_list|<
name|T
argument_list|>
name|deserializer
decl_stmt|;
DECL|field|key1
specifier|private
name|T
name|key1
decl_stmt|;
DECL|field|key2
specifier|private
name|T
name|key2
decl_stmt|;
DECL|method|DeserializerComparator (Deserializer<T> deserializer)
specifier|protected
name|DeserializerComparator
parameter_list|(
name|Deserializer
argument_list|<
name|T
argument_list|>
name|deserializer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|deserializer
operator|=
name|deserializer
expr_stmt|;
name|this
operator|.
name|deserializer
operator|.
name|open
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
try|try
block|{
name|buffer
operator|.
name|reset
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|l1
argument_list|)
expr_stmt|;
name|key1
operator|=
name|deserializer
operator|.
name|deserialize
argument_list|(
name|key1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|b2
argument_list|,
name|s2
argument_list|,
name|l2
argument_list|)
expr_stmt|;
name|key2
operator|=
name|deserializer
operator|.
name|deserialize
argument_list|(
name|key2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|compare
argument_list|(
name|key1
argument_list|,
name|key2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

