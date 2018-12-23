begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_comment
comment|/**  * PKI PKIProfile package supports different kind of profiles that certificates  * can support. If you are not familiar with PKI profiles, there is an  * excellent introduction at  *  * https://www.cs.auckland.ac.nz/~pgut001/pubs/x509guide.txt  *  * At high level, the profiles in this directory define what kinds of  * Extensions, General names , Key usage and critical extensions are  * permitted when the CA is functional.  *  * An excellent example of a profile would be ozone profile if you would  * like to see a reference to create your own profiles.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificate.authority.PKIProfiles
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|certificate
operator|.
name|authority
operator|.
name|PKIProfiles
package|;
end_package

end_unit

