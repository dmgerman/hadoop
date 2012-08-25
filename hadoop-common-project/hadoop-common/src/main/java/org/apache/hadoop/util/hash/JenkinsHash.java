begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.hash
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|hash
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
comment|/**  * Produces 32-bit hash for hash table lookup.  *   *<pre>lookup3.c, by Bob Jenkins, May 2006, Public Domain.  *  * You can use this free for any purpose.  It's in the public domain.  * It has no warranty.  *</pre>  *   * @see<a href="http://burtleburtle.net/bob/c/lookup3.c">lookup3.c</a>  * @see<a href="http://www.ddj.com/184410284">Hash Functions (and how this  * function compares to others such as CRC, MD?, etc</a>  * @see<a href="http://burtleburtle.net/bob/hash/doobs.html">Has update on the  * Dr. Dobbs Article</a>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|JenkinsHash
specifier|public
class|class
name|JenkinsHash
extends|extends
name|Hash
block|{
DECL|field|INT_MASK
specifier|private
specifier|static
name|long
name|INT_MASK
init|=
literal|0x00000000ffffffffL
decl_stmt|;
DECL|field|BYTE_MASK
specifier|private
specifier|static
name|long
name|BYTE_MASK
init|=
literal|0x00000000000000ffL
decl_stmt|;
DECL|field|_instance
specifier|private
specifier|static
name|JenkinsHash
name|_instance
init|=
operator|new
name|JenkinsHash
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|Hash
name|getInstance
parameter_list|()
block|{
return|return
name|_instance
return|;
block|}
DECL|method|rot (long val, int pos)
specifier|private
specifier|static
name|long
name|rot
parameter_list|(
name|long
name|val
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Integer
operator|.
name|rotateLeft
argument_list|(
call|(
name|int
call|)
argument_list|(
name|val
operator|&
name|INT_MASK
argument_list|)
argument_list|,
name|pos
argument_list|)
operator|)
operator|&
name|INT_MASK
operator|)
return|;
block|}
comment|/**    * taken from  hashlittle() -- hash a variable-length key into a 32-bit value    *     * @param key the key (the unaligned variable-length array of bytes)    * @param nbytes number of bytes to include in hash    * @param initval can be any integer value    * @return a 32-bit value.  Every bit of the key affects every bit of the    * return value.  Two keys differing by one or two bits will have totally    * different hash values.    *     *<p>The best hash table sizes are powers of 2.  There is no need to do mod    * a prime (mod is sooo slow!).  If you need less than 32 bits, use a bitmask.    * For example, if you need only 10 bits, do    *<code>h = (h& hashmask(10));</code>    * In which case, the hash table should have hashsize(10) elements.    *     *<p>If you are hashing n strings byte[][] k, do it like this:    * for (int i = 0, h = 0; i< n; ++i) h = hash( k[i], h);    *     *<p>By Bob Jenkins, 2006.  bob_jenkins@burtleburtle.net.  You may use this    * code any way you wish, private, educational, or commercial.  It's free.    *     *<p>Use for hash table lookup, or anything where one collision in 2^^32 is    * acceptable.  Do NOT use for cryptographic purposes.   */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|hash (byte[] key, int nbytes, int initval)
specifier|public
name|int
name|hash
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|int
name|nbytes
parameter_list|,
name|int
name|initval
parameter_list|)
block|{
name|int
name|length
init|=
name|nbytes
decl_stmt|;
name|long
name|a
decl_stmt|,
name|b
decl_stmt|,
name|c
decl_stmt|;
comment|// We use longs because we don't have unsigned ints
name|a
operator|=
name|b
operator|=
name|c
operator|=
operator|(
literal|0x00000000deadbeefL
operator|+
name|length
operator|+
name|initval
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|length
operator|>
literal|12
condition|;
name|offset
operator|+=
literal|12
operator|,
name|length
operator|-=
literal|12
control|)
block|{
name|a
operator|=
operator|(
name|a
operator|+
operator|(
name|key
index|[
name|offset
operator|+
literal|0
index|]
operator|&
name|BYTE_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|1
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|8
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|2
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|16
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|3
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|24
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|+
operator|(
name|key
index|[
name|offset
operator|+
literal|4
index|]
operator|&
name|BYTE_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|5
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|8
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|6
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|16
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|7
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|24
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|+
operator|(
name|key
index|[
name|offset
operator|+
literal|8
index|]
operator|&
name|BYTE_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|9
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|8
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|10
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|16
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|11
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|24
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
comment|/*        * mix -- mix 3 32-bit values reversibly.        * This is reversible, so any information in (a,b,c) before mix() is        * still in (a,b,c) after mix().        *         * If four pairs of (a,b,c) inputs are run through mix(), or through        * mix() in reverse, there are at least 32 bits of the output that        * are sometimes the same for one pair and different for another pair.        *         * This was tested for:        * - pairs that differed by one bit, by two bits, in any combination        *   of top bits of (a,b,c), or in any combination of bottom bits of        *   (a,b,c).        * - "differ" is defined as +, -, ^, or ~^.  For + and -, I transformed        *   the output delta to a Gray code (a^(a>>1)) so a string of 1's (as        *    is commonly produced by subtraction) look like a single 1-bit        *    difference.        * - the base values were pseudorandom, all zero but one bit set, or        *   all zero plus a counter that starts at zero.        *         * Some k values for my "a-=c; a^=rot(c,k); c+=b;" arrangement that        * satisfy this are        *     4  6  8 16 19  4        *     9 15  3 18 27 15        *    14  9  3  7 17  3        * Well, "9 15 3 18 27 15" didn't quite get 32 bits diffing for         * "differ" defined as + with a one-bit base and a two-bit delta.  I        * used http://burtleburtle.net/bob/hash/avalanche.html to choose        * the operations, constants, and arrangements of the variables.        *         * This does not achieve avalanche.  There are input bits of (a,b,c)        * that fail to affect some output bits of (a,b,c), especially of a.        * The most thoroughly mixed value is c, but it doesn't really even        * achieve avalanche in c.        *         * This allows some parallelism.  Read-after-writes are good at doubling        * the number of bits affected, so the goal of mixing pulls in the        * opposite direction as the goal of parallelism.  I did what I could.        * Rotates seem to cost as much as shifts on every machine I could lay        * my hands on, and rotates are much kinder to the top and bottom bits,        * so I used rotates.        *        * #define mix(a,b,c) \        * { \        *   a -= c;  a ^= rot(c, 4);  c += b; \        *   b -= a;  b ^= rot(a, 6);  a += c; \        *   c -= b;  c ^= rot(b, 8);  b += a; \        *   a -= c;  a ^= rot(c,16);  c += b; \        *   b -= a;  b ^= rot(a,19);  a += c; \        *   c -= b;  c ^= rot(b, 4);  b += a; \        * }        *         * mix(a,b,c);        */
name|a
operator|=
operator|(
name|a
operator|-
name|c
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|^=
name|rot
argument_list|(
name|c
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|+
name|b
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|-
name|a
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|^=
name|rot
argument_list|(
name|a
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|+
name|c
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|-
name|b
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|^=
name|rot
argument_list|(
name|b
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|+
name|a
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|-
name|c
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|^=
name|rot
argument_list|(
name|c
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|+
name|b
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|-
name|a
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|^=
name|rot
argument_list|(
name|a
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|+
name|c
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|-
name|b
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|^=
name|rot
argument_list|(
name|b
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|+
name|a
operator|)
operator|&
name|INT_MASK
expr_stmt|;
block|}
comment|//-------------------------------- last block: affect all 32 bits of (c)
switch|switch
condition|(
name|length
condition|)
block|{
comment|// all the case statements fall through
case|case
literal|12
case|:
name|c
operator|=
operator|(
name|c
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|11
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|24
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|11
case|:
name|c
operator|=
operator|(
name|c
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|10
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|16
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|10
case|:
name|c
operator|=
operator|(
name|c
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|9
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|8
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|9
case|:
name|c
operator|=
operator|(
name|c
operator|+
operator|(
name|key
index|[
name|offset
operator|+
literal|8
index|]
operator|&
name|BYTE_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|8
case|:
name|b
operator|=
operator|(
name|b
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|7
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|24
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|7
case|:
name|b
operator|=
operator|(
name|b
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|6
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|16
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|6
case|:
name|b
operator|=
operator|(
name|b
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|5
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|8
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|5
case|:
name|b
operator|=
operator|(
name|b
operator|+
operator|(
name|key
index|[
name|offset
operator|+
literal|4
index|]
operator|&
name|BYTE_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|4
case|:
name|a
operator|=
operator|(
name|a
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|3
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|24
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|3
case|:
name|a
operator|=
operator|(
name|a
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|2
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|16
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|2
case|:
name|a
operator|=
operator|(
name|a
operator|+
operator|(
operator|(
operator|(
name|key
index|[
name|offset
operator|+
literal|1
index|]
operator|&
name|BYTE_MASK
operator|)
operator|<<
literal|8
operator|)
operator|&
name|INT_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
case|case
literal|1
case|:
name|a
operator|=
operator|(
name|a
operator|+
operator|(
name|key
index|[
name|offset
operator|+
literal|0
index|]
operator|&
name|BYTE_MASK
operator|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
break|break;
case|case
literal|0
case|:
return|return
call|(
name|int
call|)
argument_list|(
name|c
operator|&
name|INT_MASK
argument_list|)
return|;
block|}
comment|/*      * final -- final mixing of 3 32-bit values (a,b,c) into c      *       * Pairs of (a,b,c) values differing in only a few bits will usually      * produce values of c that look totally different.  This was tested for      * - pairs that differed by one bit, by two bits, in any combination      *   of top bits of (a,b,c), or in any combination of bottom bits of      *   (a,b,c).      *       * - "differ" is defined as +, -, ^, or ~^.  For + and -, I transformed      *   the output delta to a Gray code (a^(a>>1)) so a string of 1's (as      *   is commonly produced by subtraction) look like a single 1-bit      *   difference.      *       * - the base values were pseudorandom, all zero but one bit set, or      *   all zero plus a counter that starts at zero.      *       * These constants passed:      *   14 11 25 16 4 14 24      *   12 14 25 16 4 14 24      * and these came close:      *    4  8 15 26 3 22 24      *   10  8 15 26 3 22 24      *   11  8 15 26 3 22 24      *       * #define final(a,b,c) \      * {       *   c ^= b; c -= rot(b,14); \      *   a ^= c; a -= rot(c,11); \      *   b ^= a; b -= rot(a,25); \      *   c ^= b; c -= rot(b,16); \      *   a ^= c; a -= rot(c,4);  \      *   b ^= a; b -= rot(a,14); \      *   c ^= b; c -= rot(b,24); \      * }      *       */
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|-
name|rot
argument_list|(
name|b
argument_list|,
literal|14
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|-
name|rot
argument_list|(
name|c
argument_list|,
literal|11
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|-
name|rot
argument_list|(
name|a
argument_list|,
literal|25
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|-
name|rot
argument_list|(
name|b
argument_list|,
literal|16
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|=
operator|(
name|a
operator|-
name|rot
argument_list|(
name|c
argument_list|,
literal|4
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|=
operator|(
name|b
operator|-
name|rot
argument_list|(
name|a
argument_list|,
literal|14
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|=
operator|(
name|c
operator|-
name|rot
argument_list|(
name|b
argument_list|,
literal|24
argument_list|)
operator|)
operator|&
name|INT_MASK
expr_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|c
operator|&
name|INT_MASK
argument_list|)
return|;
block|}
comment|/**    * Compute the hash of the specified file    * @param args name of file to compute hash of.    * @throws IOException    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: JenkinsHash filename"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|int
name|value
init|=
literal|0
decl_stmt|;
name|JenkinsHash
name|hash
init|=
operator|new
name|JenkinsHash
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|length
init|=
name|in
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
init|;
name|length
operator|>
literal|0
condition|;
name|length
operator|=
name|in
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
control|)
block|{
name|value
operator|=
name|hash
operator|.
name|hash
argument_list|(
name|bytes
argument_list|,
name|length
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

