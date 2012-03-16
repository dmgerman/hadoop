begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|Oid
import|;
end_import

begin_class
DECL|class|KerberosUtil
specifier|public
class|class
name|KerberosUtil
block|{
comment|/* Return the Kerberos login module name */
DECL|method|getKrb5LoginModuleName ()
specifier|public
specifier|static
name|String
name|getKrb5LoginModuleName
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM"
argument_list|)
condition|?
literal|"com.ibm.security.auth.module.Krb5LoginModule"
else|:
literal|"com.sun.security.auth.module.Krb5LoginModule"
return|;
block|}
DECL|method|getOidClassInstance (String servicePrincipal, GSSManager gssManager)
specifier|public
specifier|static
name|Oid
name|getOidClassInstance
parameter_list|(
name|String
name|servicePrincipal
parameter_list|,
name|GSSManager
name|gssManager
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|GSSException
throws|,
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|oidClass
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM"
argument_list|)
condition|)
block|{
name|oidClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.ibm.security.jgss.GSSUtil"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|oidClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.security.jgss.GSSUtil"
argument_list|)
expr_stmt|;
block|}
name|Field
name|oidField
init|=
name|oidClass
operator|.
name|getDeclaredField
argument_list|(
literal|"GSS_KRB5_MECH_OID"
argument_list|)
decl_stmt|;
return|return
operator|(
name|Oid
operator|)
name|oidField
operator|.
name|get
argument_list|(
name|oidClass
argument_list|)
return|;
block|}
DECL|method|getDefaultRealm ()
specifier|public
specifier|static
name|String
name|getDefaultRealm
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|NoSuchMethodException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
name|Object
name|kerbConf
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|classRef
decl_stmt|;
name|Method
name|getInstanceMethod
decl_stmt|;
name|Method
name|getDefaultRealmMethod
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM"
argument_list|)
condition|)
block|{
name|classRef
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.ibm.security.krb5.internal.Config"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|classRef
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.security.krb5.Config"
argument_list|)
expr_stmt|;
block|}
name|getInstanceMethod
operator|=
name|classRef
operator|.
name|getMethod
argument_list|(
literal|"getInstance"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|kerbConf
operator|=
name|getInstanceMethod
operator|.
name|invoke
argument_list|(
name|classRef
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|getDefaultRealmMethod
operator|=
name|classRef
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getDefaultRealm"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
operator|(
name|String
operator|)
name|getDefaultRealmMethod
operator|.
name|invoke
argument_list|(
name|kerbConf
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

