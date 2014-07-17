begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.serde
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|serde
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|nativetask
operator|.
name|INativeComparable
import|;
end_import

begin_class
DECL|class|TextSerializer
specifier|public
class|class
name|TextSerializer
implements|implements
name|INativeSerializer
argument_list|<
name|Text
argument_list|>
implements|,
name|INativeComparable
block|{
DECL|method|TextSerializer ()
specifier|public
name|TextSerializer
parameter_list|()
throws|throws
name|SecurityException
throws|,
name|NoSuchMethodException
block|{   }
annotation|@
name|Override
DECL|method|getLength (Text w)
specifier|public
name|int
name|getLength
parameter_list|(
name|Text
name|w
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|w
operator|.
name|getLength
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|serialize (Text w, DataOutput out)
specifier|public
name|void
name|serialize
parameter_list|(
name|Text
name|w
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|w
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|w
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deserialize (DataInput in, int length, Text w)
specifier|public
name|void
name|deserialize
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|length
parameter_list|,
name|Text
name|w
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|w
operator|.
name|setCapacity
argument_list|(
name|length
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|w
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
name|w
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

