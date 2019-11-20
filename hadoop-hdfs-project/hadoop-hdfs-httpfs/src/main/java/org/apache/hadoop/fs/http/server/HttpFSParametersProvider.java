begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
operator|.
name|server
package|;
end_package

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
name|fs
operator|.
name|XAttrCodec
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
name|XAttrSetFlag
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
name|http
operator|.
name|client
operator|.
name|HttpFSFileSystem
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
name|http
operator|.
name|client
operator|.
name|HttpFSFileSystem
operator|.
name|Operation
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccess
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
name|lib
operator|.
name|wsrs
operator|.
name|BooleanParam
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
name|lib
operator|.
name|wsrs
operator|.
name|EnumParam
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
name|lib
operator|.
name|wsrs
operator|.
name|EnumSetParam
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
name|lib
operator|.
name|wsrs
operator|.
name|LongParam
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
name|lib
operator|.
name|wsrs
operator|.
name|Param
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
name|lib
operator|.
name|wsrs
operator|.
name|ParametersProvider
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
name|lib
operator|.
name|wsrs
operator|.
name|ShortParam
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
name|lib
operator|.
name|wsrs
operator|.
name|StringParam
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * HttpFS ParametersProvider.  */
end_comment

begin_class
annotation|@
name|Provider
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|HttpFSParametersProvider
specifier|public
class|class
name|HttpFSParametersProvider
extends|extends
name|ParametersProvider
block|{
DECL|field|PARAMS_DEF
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Enum
argument_list|,
name|Class
argument_list|<
name|Param
argument_list|<
name|?
argument_list|>
argument_list|>
index|[]
argument_list|>
name|PARAMS_DEF
init|=
operator|new
name|HashMap
argument_list|<
name|Enum
argument_list|,
name|Class
argument_list|<
name|Param
argument_list|<
name|?
argument_list|>
argument_list|>
index|[]
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|OPEN
argument_list|,
operator|new
name|Class
index|[]
block|{
name|OffsetParam
operator|.
name|class
block|,
name|LenParam
operator|.
name|class
block|,
name|NoRedirectParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETFILESTATUS
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|LISTSTATUS
argument_list|,
operator|new
name|Class
index|[]
block|{
name|FilterParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETHOMEDIRECTORY
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETCONTENTSUMMARY
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETQUOTAUSAGE
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETFILECHECKSUM
argument_list|,
operator|new
name|Class
index|[]
block|{
name|NoRedirectParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETFILEBLOCKLOCATIONS
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETACLSTATUS
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETTRASHROOT
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|INSTRUMENTATION
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|APPEND
argument_list|,
operator|new
name|Class
index|[]
block|{
name|DataParam
operator|.
name|class
block|,
name|NoRedirectParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|CONCAT
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SourcesParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|TRUNCATE
argument_list|,
operator|new
name|Class
index|[]
block|{
name|NewLengthParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|CREATE
argument_list|,
operator|new
name|Class
index|[]
block|{
name|PermissionParam
operator|.
name|class
block|,
name|OverwriteParam
operator|.
name|class
block|,
name|ReplicationParam
operator|.
name|class
block|,
name|BlockSizeParam
operator|.
name|class
block|,
name|DataParam
operator|.
name|class
block|,
name|UnmaskedPermissionParam
operator|.
name|class
block|,
name|NoRedirectParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|MKDIRS
argument_list|,
operator|new
name|Class
index|[]
block|{
name|PermissionParam
operator|.
name|class
block|,
name|UnmaskedPermissionParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|RENAME
argument_list|,
operator|new
name|Class
index|[]
block|{
name|DestinationParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETOWNER
argument_list|,
operator|new
name|Class
index|[]
block|{
name|OwnerParam
operator|.
name|class
block|,
name|GroupParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETPERMISSION
argument_list|,
operator|new
name|Class
index|[]
block|{
name|PermissionParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETREPLICATION
argument_list|,
operator|new
name|Class
index|[]
block|{
name|ReplicationParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETTIMES
argument_list|,
operator|new
name|Class
index|[]
block|{
name|ModifiedTimeParam
operator|.
name|class
block|,
name|AccessTimeParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|DELETE
argument_list|,
operator|new
name|Class
index|[]
block|{
name|RecursiveParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETACL
argument_list|,
operator|new
name|Class
index|[]
block|{
name|AclPermissionParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|REMOVEACL
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|MODIFYACLENTRIES
argument_list|,
operator|new
name|Class
index|[]
block|{
name|AclPermissionParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|REMOVEACLENTRIES
argument_list|,
operator|new
name|Class
index|[]
block|{
name|AclPermissionParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|REMOVEDEFAULTACL
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETXATTR
argument_list|,
operator|new
name|Class
index|[]
block|{
name|XAttrNameParam
operator|.
name|class
block|,
name|XAttrValueParam
operator|.
name|class
block|,
name|XAttrSetFlagParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|REMOVEXATTR
argument_list|,
operator|new
name|Class
index|[]
block|{
name|XAttrNameParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETXATTRS
argument_list|,
operator|new
name|Class
index|[]
block|{
name|XAttrNameParam
operator|.
name|class
block|,
name|XAttrEncodingParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|LISTXATTRS
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|LISTSTATUS_BATCH
argument_list|,
operator|new
name|Class
index|[]
block|{
name|StartAfterParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETALLSTORAGEPOLICY
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETSTORAGEPOLICY
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|SETSTORAGEPOLICY
argument_list|,
operator|new
name|Class
index|[]
block|{
name|PolicyNameParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|UNSETSTORAGEPOLICY
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|ALLOWSNAPSHOT
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|DISALLOWSNAPSHOT
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|CREATESNAPSHOT
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SnapshotNameParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|DELETESNAPSHOT
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SnapshotNameParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|RENAMESNAPSHOT
argument_list|,
operator|new
name|Class
index|[]
block|{
name|OldSnapshotNameParam
operator|.
name|class
block|,
name|SnapshotNameParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETSNAPSHOTDIFF
argument_list|,
operator|new
name|Class
index|[]
block|{
name|OldSnapshotNameParam
operator|.
name|class
block|,
name|SnapshotNameParam
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETSNAPSHOTTABLEDIRECTORYLIST
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
name|PARAMS_DEF
operator|.
name|put
argument_list|(
name|Operation
operator|.
name|GETSERVERDEFAULTS
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|HttpFSParametersProvider ()
specifier|public
name|HttpFSParametersProvider
parameter_list|()
block|{
name|super
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|,
name|HttpFSFileSystem
operator|.
name|Operation
operator|.
name|class
argument_list|,
name|PARAMS_DEF
argument_list|)
expr_stmt|;
block|}
comment|/**    * Class for access-time parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AccessTimeParam
specifier|public
specifier|static
class|class
name|AccessTimeParam
extends|extends
name|LongParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|ACCESS_TIME_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|AccessTimeParam ()
specifier|public
name|AccessTimeParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
operator|-
literal|1l
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for block-size parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockSizeParam
specifier|public
specifier|static
class|class
name|BlockSizeParam
extends|extends
name|LongParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|BLOCKSIZE_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|BlockSizeParam ()
specifier|public
name|BlockSizeParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
operator|-
literal|1l
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for data parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DataParam
specifier|public
specifier|static
class|class
name|DataParam
extends|extends
name|BooleanParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"data"
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|DataParam ()
specifier|public
name|DataParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for noredirect parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NoRedirectParam
specifier|public
specifier|static
class|class
name|NoRedirectParam
extends|extends
name|BooleanParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"noredirect"
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|NoRedirectParam ()
specifier|public
name|NoRedirectParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for operation parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OperationParam
specifier|public
specifier|static
class|class
name|OperationParam
extends|extends
name|EnumParam
argument_list|<
name|HttpFSFileSystem
operator|.
name|Operation
argument_list|>
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|OP_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|OperationParam (String operation)
specifier|public
name|OperationParam
parameter_list|(
name|String
name|operation
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|HttpFSFileSystem
operator|.
name|Operation
operator|.
name|class
argument_list|,
name|HttpFSFileSystem
operator|.
name|Operation
operator|.
name|valueOf
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|operation
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for delete's recursive parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RecursiveParam
specifier|public
specifier|static
class|class
name|RecursiveParam
extends|extends
name|BooleanParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|RECURSIVE_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|RecursiveParam ()
specifier|public
name|RecursiveParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for filter parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FilterParam
specifier|public
specifier|static
class|class
name|FilterParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"filter"
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|FilterParam ()
specifier|public
name|FilterParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for group parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|GroupParam
specifier|public
specifier|static
class|class
name|GroupParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|GROUP_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|GroupParam ()
specifier|public
name|GroupParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for len parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LenParam
specifier|public
specifier|static
class|class
name|LenParam
extends|extends
name|LongParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"length"
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|LenParam ()
specifier|public
name|LenParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
operator|-
literal|1l
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for modified-time parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ModifiedTimeParam
specifier|public
specifier|static
class|class
name|ModifiedTimeParam
extends|extends
name|LongParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|MODIFICATION_TIME_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|ModifiedTimeParam ()
specifier|public
name|ModifiedTimeParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
operator|-
literal|1l
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for offset parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OffsetParam
specifier|public
specifier|static
class|class
name|OffsetParam
extends|extends
name|LongParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"offset"
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|OffsetParam ()
specifier|public
name|OffsetParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for newlength parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NewLengthParam
specifier|public
specifier|static
class|class
name|NewLengthParam
extends|extends
name|LongParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|NEW_LENGTH_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|NewLengthParam ()
specifier|public
name|NewLengthParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for overwrite parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OverwriteParam
specifier|public
specifier|static
class|class
name|OverwriteParam
extends|extends
name|BooleanParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|OVERWRITE_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|OverwriteParam ()
specifier|public
name|OverwriteParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for owner parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OwnerParam
specifier|public
specifier|static
class|class
name|OwnerParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|OWNER_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|OwnerParam ()
specifier|public
name|OwnerParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for permission parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|PermissionParam
specifier|public
specifier|static
class|class
name|PermissionParam
extends|extends
name|ShortParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|PERMISSION_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|PermissionParam ()
specifier|public
name|PermissionParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|HttpFSFileSystem
operator|.
name|DEFAULT_PERMISSION
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for unmaskedpermission parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|UnmaskedPermissionParam
specifier|public
specifier|static
class|class
name|UnmaskedPermissionParam
extends|extends
name|ShortParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|UNMASKED_PERMISSION_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|UnmaskedPermissionParam ()
specifier|public
name|UnmaskedPermissionParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for AclPermission parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AclPermissionParam
specifier|public
specifier|static
class|class
name|AclPermissionParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|ACLSPEC_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|AclPermissionParam ()
specifier|public
name|AclPermissionParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|HttpFSFileSystem
operator|.
name|ACLSPEC_DEFAULT
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
operator|.
name|getFileSystemConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_ACL_PERMISSION_PATTERN_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_ACL_PERMISSION_PATTERN_DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for replication parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ReplicationParam
specifier|public
specifier|static
class|class
name|ReplicationParam
extends|extends
name|ShortParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|REPLICATION_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|ReplicationParam ()
specifier|public
name|ReplicationParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for concat sources parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SourcesParam
specifier|public
specifier|static
class|class
name|SourcesParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|SOURCES_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|SourcesParam ()
specifier|public
name|SourcesParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for to-path parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DestinationParam
specifier|public
specifier|static
class|class
name|DestinationParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|DESTINATION_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|DestinationParam ()
specifier|public
name|DestinationParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for xattr parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XAttrNameParam
specifier|public
specifier|static
class|class
name|XAttrNameParam
extends|extends
name|StringParam
block|{
DECL|field|XATTR_NAME_REGX
specifier|public
specifier|static
specifier|final
name|String
name|XATTR_NAME_REGX
init|=
literal|"^(user\\.|trusted\\.|system\\.|security\\.).+"
decl_stmt|;
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|XATTR_NAME_PARAM
decl_stmt|;
DECL|field|pattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|XATTR_NAME_REGX
argument_list|)
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|XAttrNameParam ()
specifier|public
name|XAttrNameParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for xattr parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XAttrValueParam
specifier|public
specifier|static
class|class
name|XAttrValueParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|XATTR_VALUE_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|XAttrValueParam ()
specifier|public
name|XAttrValueParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for xattr parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XAttrSetFlagParam
specifier|public
specifier|static
class|class
name|XAttrSetFlagParam
extends|extends
name|EnumSetParam
argument_list|<
name|XAttrSetFlag
argument_list|>
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|XATTR_SET_FLAG_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|XAttrSetFlagParam ()
specifier|public
name|XAttrSetFlagParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|XAttrSetFlag
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for xattr parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XAttrEncodingParam
specifier|public
specifier|static
class|class
name|XAttrEncodingParam
extends|extends
name|EnumParam
argument_list|<
name|XAttrCodec
argument_list|>
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|XATTR_ENCODING_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|XAttrEncodingParam ()
specifier|public
name|XAttrEncodingParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|XAttrCodec
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for startafter parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StartAfterParam
specifier|public
specifier|static
class|class
name|StartAfterParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|START_AFTER_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|StartAfterParam ()
specifier|public
name|StartAfterParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for policyName parameter.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|PolicyNameParam
specifier|public
specifier|static
class|class
name|PolicyNameParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|POLICY_NAME_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|PolicyNameParam ()
specifier|public
name|PolicyNameParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for SnapshotName parameter.    */
DECL|class|SnapshotNameParam
specifier|public
specifier|static
class|class
name|SnapshotNameParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|SNAPSHOT_NAME_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|SnapshotNameParam ()
specifier|public
name|SnapshotNameParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class for OldSnapshotName parameter.    */
DECL|class|OldSnapshotNameParam
specifier|public
specifier|static
class|class
name|OldSnapshotNameParam
extends|extends
name|StringParam
block|{
comment|/**      * Parameter name.      */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|HttpFSFileSystem
operator|.
name|OLD_SNAPSHOT_NAME_PARAM
decl_stmt|;
comment|/**      * Constructor.      */
DECL|method|OldSnapshotNameParam ()
specifier|public
name|OldSnapshotNameParam
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

