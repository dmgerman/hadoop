begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|HistogramRawTestData
class|class
name|HistogramRawTestData
block|{
DECL|field|data
name|List
argument_list|<
name|Long
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|percentiles
name|List
argument_list|<
name|Integer
argument_list|>
name|percentiles
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|scale
name|int
name|scale
decl_stmt|;
DECL|method|getPercentiles ()
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getPercentiles
parameter_list|()
block|{
return|return
name|percentiles
return|;
block|}
DECL|method|setPercentiles (List<Integer> percentiles)
specifier|public
name|void
name|setPercentiles
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|percentiles
parameter_list|)
block|{
name|this
operator|.
name|percentiles
operator|=
name|percentiles
expr_stmt|;
block|}
DECL|method|getScale ()
specifier|public
name|int
name|getScale
parameter_list|()
block|{
return|return
name|scale
return|;
block|}
DECL|method|setScale (int scale)
specifier|public
name|void
name|setScale
parameter_list|(
name|int
name|scale
parameter_list|)
block|{
name|this
operator|.
name|scale
operator|=
name|scale
expr_stmt|;
block|}
DECL|method|getData ()
specifier|public
name|List
argument_list|<
name|Long
argument_list|>
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
DECL|method|setData (List<Long> data)
specifier|public
name|void
name|setData
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
block|}
end_class

end_unit

