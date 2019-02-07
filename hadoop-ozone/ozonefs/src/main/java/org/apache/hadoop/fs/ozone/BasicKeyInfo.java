begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
package|;
end_package

begin_comment
comment|/**  * Minimum set of Ozone key information attributes.  *<p>  * This class doesn't depend on any other ozone class just on primitive  * java types. It could be used easily in the signature of OzoneClientAdapter  * as even if a separated class loader is loaded it it won't cause any  * dependency problem.  */
end_comment

begin_class
DECL|class|BasicKeyInfo
specifier|public
class|class
name|BasicKeyInfo
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|modificationTime
specifier|private
name|long
name|modificationTime
decl_stmt|;
DECL|field|dataSize
specifier|private
name|long
name|dataSize
decl_stmt|;
DECL|method|BasicKeyInfo (String name, long modificationTime, long size)
specifier|public
name|BasicKeyInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
name|this
operator|.
name|dataSize
operator|=
name|size
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modificationTime
return|;
block|}
DECL|method|getDataSize ()
specifier|public
name|long
name|getDataSize
parameter_list|()
block|{
return|return
name|dataSize
return|;
block|}
block|}
end_class

end_unit

