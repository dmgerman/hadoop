begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
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
name|InputStream
import|;
end_import

begin_comment
comment|/**  * An InputStream covering a range of compressed data. The start and end  * offsets requested by a client may be modified by the codec to fit block  * boundaries or other algorithm-dependent requirements.  */
end_comment

begin_class
DECL|class|SplitCompressionInputStream
specifier|public
specifier|abstract
class|class
name|SplitCompressionInputStream
extends|extends
name|CompressionInputStream
block|{
DECL|field|start
specifier|private
name|long
name|start
decl_stmt|;
DECL|field|end
specifier|private
name|long
name|end
decl_stmt|;
DECL|method|SplitCompressionInputStream (InputStream in, long start, long end)
specifier|public
name|SplitCompressionInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
DECL|method|setStart (long start)
specifier|protected
name|void
name|setStart
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
DECL|method|setEnd (long end)
specifier|protected
name|void
name|setEnd
parameter_list|(
name|long
name|end
parameter_list|)
block|{
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
comment|/**    * After calling createInputStream, the values of start or end    * might change.  So this method can be used to get the new value of start.    * @return The changed value of start    */
DECL|method|getAdjustedStart ()
specifier|public
name|long
name|getAdjustedStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
comment|/**    * After calling createInputStream, the values of start or end    * might change.  So this method can be used to get the new value of end.    * @return The changed value of end    */
DECL|method|getAdjustedEnd ()
specifier|public
name|long
name|getAdjustedEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
block|}
end_class

end_unit

