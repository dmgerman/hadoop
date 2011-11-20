begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
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
name|offlineEditsViewer
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

begin_comment
comment|/**  * An EditsLoader can read a Hadoop EditLog file  and walk over its  * structure using the supplied EditsVisitor.  *  * Each implementation of EditsLoader is designed to rapidly process an  * edits log file.  As long as minor changes are made from one layout version  * to another, it is acceptable to tweak one implementation to read the next.  * However, if the layout version changes enough that it would make a  * processor slow or difficult to read, another processor should be created.  * This allows each processor to quickly read an edits log without getting  * bogged down in dealing with significant differences between layout versions.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|EditsLoader
interface|interface
name|EditsLoader
block|{
comment|/**    * Loads the edits file    */
DECL|method|loadEdits ()
specifier|public
name|void
name|loadEdits
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Can this processor handle the specified version of EditLog file?    *    * @param version EditLog version file    * @return True if this instance can process the file    */
DECL|method|canLoadVersion (int version)
specifier|public
name|boolean
name|canLoadVersion
parameter_list|(
name|int
name|version
parameter_list|)
function_decl|;
comment|/**    * Factory for obtaining version of edits log loader that can read    * a particular edits log format.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|LoaderFactory
specifier|public
class|class
name|LoaderFactory
block|{
comment|// Java doesn't support static methods on interfaces, which necessitates
comment|// this factory class
comment|/**      * Create an edits log loader, at this point we only have one,      * we might need to add more later      *      * @param v an instance of EditsVisitor (binary, XML etc.)      * @return EditsLoader that can interpret specified version, or null      */
DECL|method|getLoader (EditsVisitor v)
specifier|static
specifier|public
name|EditsLoader
name|getLoader
parameter_list|(
name|EditsVisitor
name|v
parameter_list|)
block|{
return|return
operator|new
name|EditsLoaderCurrent
argument_list|(
name|v
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

