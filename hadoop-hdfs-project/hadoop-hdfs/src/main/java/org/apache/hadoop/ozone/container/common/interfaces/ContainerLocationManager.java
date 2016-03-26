begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Returns physical path locations, where the containers will be created.  */
end_comment

begin_interface
DECL|interface|ContainerLocationManager
specifier|public
interface|interface
name|ContainerLocationManager
block|{
comment|/**    * Returns the path where the container should be placed from a set of    * locations.    *    * @return A path where we should place this container and metadata.    * @throws IOException    */
DECL|method|getContainerPath ()
name|Path
name|getContainerPath
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the path where the container Data file are stored.    *    * @return a path where we place the LevelDB and data files of a container.    * @throws IOException    */
DECL|method|getDataPath (String containerName)
name|Path
name|getDataPath
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

