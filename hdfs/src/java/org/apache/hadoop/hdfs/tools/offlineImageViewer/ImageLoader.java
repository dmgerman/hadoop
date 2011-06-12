begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * An ImageLoader can accept a DataInputStream to an Hadoop FSImage file  * and walk over its structure using the supplied ImageVisitor.  *  * Each implementation of ImageLoader is designed to rapidly process an  * image file.  As long as minor changes are made from one layout version  * to another, it is acceptable to tweak one implementation to read the next.  * However, if the layout version changes enough that it would make a  * processor slow or difficult to read, another processor should be created.  * This allows each processor to quickly read an image without getting  * bogged down in dealing with significant differences between layout versions.  */
end_comment

begin_interface
DECL|interface|ImageLoader
interface|interface
name|ImageLoader
block|{
comment|/**    * @param in DataInputStream pointing to an Hadoop FSImage file    * @param v Visit to apply to the FSImage file    * @param enumerateBlocks Should visitor visit each of the file blocks?    */
DECL|method|loadImage (DataInputStream in, ImageVisitor v, boolean enumerateBlocks)
specifier|public
name|void
name|loadImage
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|boolean
name|enumerateBlocks
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Can this processor handle the specified version of FSImage file?    *    * @param version FSImage version file    * @return True if this instance can process the file    */
DECL|method|canLoadVersion (int version)
specifier|public
name|boolean
name|canLoadVersion
parameter_list|(
name|int
name|version
parameter_list|)
function_decl|;
comment|/**    * Factory for obtaining version of image loader that can read    * a particular image format.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LoaderFactory
specifier|public
class|class
name|LoaderFactory
block|{
comment|// Java doesn't support static methods on interfaces, which necessitates
comment|// this factory class
comment|/**      * Find an image loader capable of interpreting the specified      * layout version number.  If none, return null;      *      * @param version fsimage layout version number to be processed      * @return ImageLoader that can interpret specified version, or null      */
DECL|method|getLoader (int version)
specifier|static
specifier|public
name|ImageLoader
name|getLoader
parameter_list|(
name|int
name|version
parameter_list|)
block|{
comment|// Easy to add more image processors as they are written
name|ImageLoader
index|[]
name|loaders
init|=
block|{
operator|new
name|ImageLoaderCurrent
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|ImageLoader
name|l
range|:
name|loaders
control|)
block|{
if|if
condition|(
name|l
operator|.
name|canLoadVersion
argument_list|(
name|version
argument_list|)
condition|)
return|return
name|l
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_interface

end_unit

