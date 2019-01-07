begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|delegation
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|conf
operator|.
name|Configuration
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
name|s3a
operator|.
name|S3AFileSystem
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
name|security
operator|.
name|UserGroupInformation
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * This is the base class for both the delegation binding  * code and the back end service created; allows for  * shared methods across both.  *  * The lifecycle sequence is as follows  *<pre>  *   - create  *   - bindToFileSystem(uri, ownerFS)  *   - init  *   - start  *   ...api calls...  *   - stop  *</pre>  *  * As the S3ADelegation mechanism is all configured during the filesystem  * initalize() operation, it is not ready for use through all the start process.  */
end_comment

begin_class
DECL|class|AbstractDTService
specifier|public
specifier|abstract
class|class
name|AbstractDTService
extends|extends
name|AbstractService
block|{
comment|/**    * URI of the filesystem.    * Valid after {@link #bindToFileSystem(URI, S3AFileSystem)}.    */
DECL|field|canonicalUri
specifier|private
name|URI
name|canonicalUri
decl_stmt|;
comment|/**    * The owning filesystem.    * Valid after {@link #bindToFileSystem(URI, S3AFileSystem)}.    */
DECL|field|fileSystem
specifier|private
name|S3AFileSystem
name|fileSystem
decl_stmt|;
comment|/**    * Owner of the filesystem.    * Valid after {@link #bindToFileSystem(URI, S3AFileSystem)}.    */
DECL|field|owner
specifier|private
name|UserGroupInformation
name|owner
decl_stmt|;
comment|/**    * Protected constructor.    * @param name service name.    */
DECL|method|AbstractDTService (final String name)
specifier|protected
name|AbstractDTService
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Bind to the filesystem.    * Subclasses can use this to perform their own binding operations -    * but they must always call their superclass implementation.    * This<i>Must</i> be called before calling {@code init()}.    *    *<b>Important:</b>    * This binding will happen during FileSystem.initialize(); the FS    * is not live for actual use and will not yet have interacted with    * AWS services.    * @param uri the canonical URI of the FS.    * @param fs owning FS.    * @throws IOException failure.    */
DECL|method|bindToFileSystem ( final URI uri, final S3AFileSystem fs)
specifier|public
name|void
name|bindToFileSystem
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|S3AFileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|requireServiceState
argument_list|(
name|STATE
operator|.
name|NOTINITED
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|canonicalUri
operator|==
literal|null
argument_list|,
literal|"bindToFileSystem called twice"
argument_list|)
expr_stmt|;
name|this
operator|.
name|canonicalUri
operator|=
name|requireNonNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileSystem
operator|=
name|requireNonNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|fs
operator|.
name|getOwner
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the canonical URI of the filesystem, which is what is    * used to identify the tokens.    * @return the URI.    */
DECL|method|getCanonicalUri ()
specifier|public
name|URI
name|getCanonicalUri
parameter_list|()
block|{
return|return
name|canonicalUri
return|;
block|}
comment|/**    * Get the owner of the FS.    * @return the owner fs    */
DECL|method|getFileSystem ()
specifier|protected
name|S3AFileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
name|fileSystem
return|;
block|}
comment|/**    * Get the owner of this Service.    * @return owner; non-null after binding to an FS.    */
DECL|method|getOwner ()
specifier|public
name|UserGroupInformation
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Require that the service is in a given state.    * @param state desired state.    * @throws IllegalStateException if the condition is not met    */
DECL|method|requireServiceState (final STATE state)
specifier|protected
name|void
name|requireServiceState
parameter_list|(
specifier|final
name|STATE
name|state
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isInState
argument_list|(
name|state
argument_list|)
argument_list|,
literal|"Required State: %s; Actual State %s"
argument_list|,
name|state
argument_list|,
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Require the service to be started.    * @throws IllegalStateException if it is not.    */
DECL|method|requireServiceStarted ()
specifier|protected
name|void
name|requireServiceStarted
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|requireServiceState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (final Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|requireNonNull
argument_list|(
name|canonicalUri
argument_list|,
literal|"service does not have a canonical URI"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

