begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|model
operator|.
name|OSSObjectSummary
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
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|AliyunOSSUtils
operator|.
name|objectRepresentsDirectory
import|;
end_import

begin_comment
comment|/**  * Interface to implement by the logic deciding whether to accept a summary  * entry or path as a valid file or directory.  */
end_comment

begin_interface
DECL|interface|FileStatusAcceptor
specifier|public
interface|interface
name|FileStatusAcceptor
block|{
comment|/**    * Predicate to decide whether or not to accept a summary entry.    * @param keyPath qualified path to the entry    * @param summary summary entry    * @return true if the entry is accepted (i.e. that a status entry    * should be generated.    */
DECL|method|accept (Path keyPath, OSSObjectSummary summary)
name|boolean
name|accept
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|OSSObjectSummary
name|summary
parameter_list|)
function_decl|;
comment|/**    * Predicate to decide whether or not to accept a prefix.    * @param keyPath qualified path to the entry    * @param commonPrefix the prefix    * @return true if the entry is accepted (i.e. that a status entry    * should be generated.)    */
DECL|method|accept (Path keyPath, String commonPrefix)
name|boolean
name|accept
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|String
name|commonPrefix
parameter_list|)
function_decl|;
comment|/**    * Accept all entries except the base path.    */
DECL|class|AcceptFilesOnly
class|class
name|AcceptFilesOnly
implements|implements
name|FileStatusAcceptor
block|{
DECL|field|qualifiedPath
specifier|private
specifier|final
name|Path
name|qualifiedPath
decl_stmt|;
DECL|method|AcceptFilesOnly (Path qualifiedPath)
specifier|public
name|AcceptFilesOnly
parameter_list|(
name|Path
name|qualifiedPath
parameter_list|)
block|{
name|this
operator|.
name|qualifiedPath
operator|=
name|qualifiedPath
expr_stmt|;
block|}
comment|/**      * Reject a summary entry if the key path is the qualified Path.      * @param keyPath key path of the entry      * @param summary summary entry      * @return true if the entry is accepted (i.e. that a status entry      * should be generated.      */
annotation|@
name|Override
DECL|method|accept (Path keyPath, OSSObjectSummary summary)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|OSSObjectSummary
name|summary
parameter_list|)
block|{
return|return
operator|!
name|keyPath
operator|.
name|equals
argument_list|(
name|qualifiedPath
argument_list|)
operator|&&
operator|!
name|objectRepresentsDirectory
argument_list|(
name|summary
operator|.
name|getKey
argument_list|()
argument_list|,
name|summary
operator|.
name|getSize
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Accept no directory paths.      * @param keyPath qualified path to the entry      * @param prefix common prefix in listing.      * @return false, always.      */
annotation|@
name|Override
DECL|method|accept (Path keyPath, String prefix)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Accept all entries except the base path.    */
DECL|class|AcceptAllButSelf
class|class
name|AcceptAllButSelf
implements|implements
name|FileStatusAcceptor
block|{
comment|/** Base path. */
DECL|field|qualifiedPath
specifier|private
specifier|final
name|Path
name|qualifiedPath
decl_stmt|;
comment|/**      * Constructor.      * @param qualifiedPath an already-qualified path.      */
DECL|method|AcceptAllButSelf (Path qualifiedPath)
specifier|public
name|AcceptAllButSelf
parameter_list|(
name|Path
name|qualifiedPath
parameter_list|)
block|{
name|this
operator|.
name|qualifiedPath
operator|=
name|qualifiedPath
expr_stmt|;
block|}
comment|/**      * Reject a summary entry if the key path is the qualified Path.      * @param keyPath key path of the entry      * @param summary summary entry      * @return true if the entry is accepted (i.e. that a status entry      * should be generated.)      */
annotation|@
name|Override
DECL|method|accept (Path keyPath, OSSObjectSummary summary)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|OSSObjectSummary
name|summary
parameter_list|)
block|{
return|return
operator|!
name|keyPath
operator|.
name|equals
argument_list|(
name|qualifiedPath
argument_list|)
return|;
block|}
comment|/**      * Accept all prefixes except the one for the base path, "self".      * @param keyPath qualified path to the entry      * @param prefix common prefix in listing.      * @return true if the entry is accepted (i.e. that a status entry      * should be generated.      */
annotation|@
name|Override
DECL|method|accept (Path keyPath, String prefix)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|!
name|keyPath
operator|.
name|equals
argument_list|(
name|qualifiedPath
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

