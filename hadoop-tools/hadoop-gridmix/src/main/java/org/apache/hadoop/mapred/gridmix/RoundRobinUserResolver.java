begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|FileSystem
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
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
name|util
operator|.
name|LineReader
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

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
name|Collections
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
name|List
import|;
end_import

begin_class
DECL|class|RoundRobinUserResolver
specifier|public
class|class
name|RoundRobinUserResolver
implements|implements
name|UserResolver
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RoundRobinUserResolver
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|uidx
specifier|private
name|int
name|uidx
init|=
literal|0
decl_stmt|;
DECL|field|users
specifier|private
name|List
argument_list|<
name|UserGroupInformation
argument_list|>
name|users
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|/**    *  Mapping between user names of original cluster and UGIs of proxy users of    *  simulated cluster    */
DECL|field|usercache
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|UserGroupInformation
argument_list|>
name|usercache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|UserGroupInformation
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Userlist assumes one user per line.    * Each line in users-list-file is of the form&lt;username&gt;[,group]*     *<br> Group names are ignored(they are not parsed at all).    */
DECL|method|parseUserList (URI userUri, Configuration conf)
specifier|private
name|List
argument_list|<
name|UserGroupInformation
argument_list|>
name|parseUserList
parameter_list|(
name|URI
name|userUri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|userUri
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|final
name|Path
name|userloc
init|=
operator|new
name|Path
argument_list|(
name|userUri
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Text
name|rawUgi
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|userloc
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|UserGroupInformation
argument_list|>
name|ugiList
init|=
operator|new
name|ArrayList
argument_list|<
name|UserGroupInformation
argument_list|>
argument_list|()
decl_stmt|;
name|LineReader
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|LineReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|userloc
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|in
operator|.
name|readLine
argument_list|(
name|rawUgi
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|//line is of the form username[,group]*
if|if
condition|(
name|rawUgi
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
continue|continue;
comment|//Continue on empty line
block|}
comment|// e is end position of user name in this line
name|int
name|e
init|=
name|rawUgi
operator|.
name|find
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Missing username: "
operator|+
name|rawUgi
argument_list|)
throw|;
block|}
if|if
condition|(
name|e
operator|==
operator|-
literal|1
condition|)
block|{
name|e
operator|=
name|rawUgi
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|username
init|=
name|Text
operator|.
name|decode
argument_list|(
name|rawUgi
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|e
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|username
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while creating a proxy user "
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ugi
operator|!=
literal|null
condition|)
block|{
name|ugiList
operator|.
name|add
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
block|}
comment|// No need to parse groups, even if they exist. Go to next line
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ugiList
return|;
block|}
annotation|@
name|Override
DECL|method|setTargetUsers (URI userloc, Configuration conf)
specifier|public
specifier|synchronized
name|boolean
name|setTargetUsers
parameter_list|(
name|URI
name|userloc
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|uidx
operator|=
literal|0
expr_stmt|;
name|users
operator|=
name|parseUserList
argument_list|(
name|userloc
argument_list|,
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|users
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|buildEmptyUsersErrorMsg
argument_list|(
name|userloc
argument_list|)
argument_list|)
throw|;
block|}
name|usercache
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|buildEmptyUsersErrorMsg (URI userloc)
specifier|static
name|String
name|buildEmptyUsersErrorMsg
parameter_list|(
name|URI
name|userloc
parameter_list|)
block|{
return|return
literal|"Empty user list is not allowed for RoundRobinUserResolver. Provided"
operator|+
literal|" user resource URI '"
operator|+
name|userloc
operator|+
literal|"' resulted in an empty user list."
return|;
block|}
annotation|@
name|Override
DECL|method|getTargetUgi ( UserGroupInformation ugi)
specifier|public
specifier|synchronized
name|UserGroupInformation
name|getTargetUgi
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
comment|// UGI of proxy user
name|UserGroupInformation
name|targetUGI
init|=
name|usercache
operator|.
name|get
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetUGI
operator|==
literal|null
condition|)
block|{
name|targetUGI
operator|=
name|users
operator|.
name|get
argument_list|(
name|uidx
operator|++
operator|%
name|users
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|usercache
operator|.
name|put
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|,
name|targetUGI
argument_list|)
expr_stmt|;
block|}
return|return
name|targetUGI
return|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * {@link RoundRobinUserResolver} needs to map the users in the    * trace to the provided list of target users. So user list is needed.    */
DECL|method|needsTargetUsersList ()
specifier|public
name|boolean
name|needsTargetUsersList
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

