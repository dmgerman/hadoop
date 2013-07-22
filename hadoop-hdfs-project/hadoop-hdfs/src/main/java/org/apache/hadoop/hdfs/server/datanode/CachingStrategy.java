begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
package|;
end_package

begin_comment
comment|/**  * The caching strategy we should use for an HDFS read or write operation.  */
end_comment

begin_class
DECL|class|CachingStrategy
specifier|public
class|class
name|CachingStrategy
block|{
DECL|field|dropBehind
specifier|private
name|Boolean
name|dropBehind
decl_stmt|;
comment|// null = use server defaults
DECL|field|readahead
specifier|private
name|Long
name|readahead
decl_stmt|;
comment|// null = use server defaults
DECL|method|newDefaultStrategy ()
specifier|public
specifier|static
name|CachingStrategy
name|newDefaultStrategy
parameter_list|()
block|{
return|return
operator|new
name|CachingStrategy
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newDropBehind ()
specifier|public
specifier|static
name|CachingStrategy
name|newDropBehind
parameter_list|()
block|{
return|return
operator|new
name|CachingStrategy
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|duplicate ()
specifier|public
name|CachingStrategy
name|duplicate
parameter_list|()
block|{
return|return
operator|new
name|CachingStrategy
argument_list|(
name|this
operator|.
name|dropBehind
argument_list|,
name|this
operator|.
name|readahead
argument_list|)
return|;
block|}
DECL|method|CachingStrategy (Boolean dropBehind, Long readahead)
specifier|public
name|CachingStrategy
parameter_list|(
name|Boolean
name|dropBehind
parameter_list|,
name|Long
name|readahead
parameter_list|)
block|{
name|this
operator|.
name|dropBehind
operator|=
name|dropBehind
expr_stmt|;
name|this
operator|.
name|readahead
operator|=
name|readahead
expr_stmt|;
block|}
DECL|method|getDropBehind ()
specifier|public
name|Boolean
name|getDropBehind
parameter_list|()
block|{
return|return
name|dropBehind
return|;
block|}
DECL|method|setDropBehind (Boolean dropBehind)
specifier|public
name|void
name|setDropBehind
parameter_list|(
name|Boolean
name|dropBehind
parameter_list|)
block|{
name|this
operator|.
name|dropBehind
operator|=
name|dropBehind
expr_stmt|;
block|}
DECL|method|getReadahead ()
specifier|public
name|Long
name|getReadahead
parameter_list|()
block|{
return|return
name|readahead
return|;
block|}
DECL|method|setReadahead (Long readahead)
specifier|public
name|void
name|setReadahead
parameter_list|(
name|Long
name|readahead
parameter_list|)
block|{
name|this
operator|.
name|readahead
operator|=
name|readahead
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CachingStrategy(dropBehind="
operator|+
name|dropBehind
operator|+
literal|", readahead="
operator|+
name|readahead
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

