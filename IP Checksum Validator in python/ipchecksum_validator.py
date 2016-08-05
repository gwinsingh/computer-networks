def ip_checksum(words):
 
    #split into 16-bit words   
    #words = splitN(''.join(ip_header.split()),4) # splits string into 16 bit words
    words[5]='0000'
    #print "\n\nGiven set of 16 bit values is:"
    #print words
    #print "\n\n"
 
    csum = 0;
    for word in words:
        csum += int(word, base=16)
 
    csum += (csum >> 16)          #add nibble
    csum = csum & 0xFFFF ^ 0xFFFF #trim to 16 bits and complement
 
    return csum
 

#header='4500 003c 1c46 4000 4006 b1e6 ac10 0a63 ac10 0a0c'
#header = '45 00 00 e8 00 00 40 00 40 11 00 00 0a 86 33 f1 0a 86 33 76'
print "Enter the 40 hex character data:\n"
header=raw_input()
header=str(header)
words=header.split()

s_sum=int(words[5],base=16)
cal_csum=ip_checksum(words)

print "Calculated Check Sum is : %x"%cal_csum #prints "bd92"

print "Supposed Check Sum is : %x\n"%s_sum

if s_sum == cal_csum:
	print "***\tCheckSum is Valid!!"
	pass
else:
	print "***\tCheckSum is InValid"
