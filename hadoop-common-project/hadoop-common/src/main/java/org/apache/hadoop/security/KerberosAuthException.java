begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UGIExceptionMessages
operator|.
name|*
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
comment|/**  * Thrown when {@link UserGroupInformation} failed with an unrecoverable error,  * such as failure in kerberos login/logout, invalid subject etc.  *  * Caller should not retry when catching this exception.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|KerberosAuthException
specifier|public
class|class
name|KerberosAuthException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|31L
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|principal
specifier|private
name|String
name|principal
decl_stmt|;
DECL|field|keytabFile
specifier|private
name|String
name|keytabFile
decl_stmt|;
DECL|field|ticketCacheFile
specifier|private
name|String
name|ticketCacheFile
decl_stmt|;
DECL|field|initialMessage
specifier|private
name|String
name|initialMessage
decl_stmt|;
DECL|method|KerberosAuthException (String msg)
specifier|public
name|KerberosAuthException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|KerberosAuthException (Throwable cause)
specifier|public
name|KerberosAuthException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|KerberosAuthException (String initialMsg, Throwable cause)
specifier|public
name|KerberosAuthException
parameter_list|(
name|String
name|initialMsg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|initialMessage
operator|=
name|initialMsg
expr_stmt|;
block|}
DECL|method|setUser (final String u)
specifier|public
name|void
name|setUser
parameter_list|(
specifier|final
name|String
name|u
parameter_list|)
block|{
name|user
operator|=
name|u
expr_stmt|;
block|}
DECL|method|setPrincipal (final String p)
specifier|public
name|void
name|setPrincipal
parameter_list|(
specifier|final
name|String
name|p
parameter_list|)
block|{
name|principal
operator|=
name|p
expr_stmt|;
block|}
DECL|method|setKeytabFile (final String k)
specifier|public
name|void
name|setKeytabFile
parameter_list|(
specifier|final
name|String
name|k
parameter_list|)
block|{
name|keytabFile
operator|=
name|k
expr_stmt|;
block|}
DECL|method|setTicketCacheFile (final String t)
specifier|public
name|void
name|setTicketCacheFile
parameter_list|(
specifier|final
name|String
name|t
parameter_list|)
block|{
name|ticketCacheFile
operator|=
name|t
expr_stmt|;
block|}
comment|/** @return The initial message, or null if not set. */
DECL|method|getInitialMessage ()
specifier|public
name|String
name|getInitialMessage
parameter_list|()
block|{
return|return
name|initialMessage
return|;
block|}
comment|/** @return The keytab file path, or null if not set. */
DECL|method|getKeytabFile ()
specifier|public
name|String
name|getKeytabFile
parameter_list|()
block|{
return|return
name|keytabFile
return|;
block|}
comment|/** @return The principal, or null if not set. */
DECL|method|getPrincipal ()
specifier|public
name|String
name|getPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
comment|/** @return The ticket cache file path, or null if not set. */
DECL|method|getTicketCacheFile ()
specifier|public
name|String
name|getTicketCacheFile
parameter_list|()
block|{
return|return
name|ticketCacheFile
return|;
block|}
comment|/** @return The user, or null if not set. */
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|initialMessage
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|initialMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|FOR_USER
operator|+
name|user
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|FOR_PRINCIPAL
operator|+
name|principal
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keytabFile
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|FROM_KEYTAB
operator|+
name|keytabFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ticketCacheFile
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|USING_TICKET_CACHE_FILE
operator|+
name|ticketCacheFile
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|super
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

