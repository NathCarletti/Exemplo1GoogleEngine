package br.com.caroltr.gae_exemplo1.Service;


import br.com.caroltr.gae_exemplo1.Repository.UserRepository;
import br.com.caroltr.gae_exemplo1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service ("userDetailsService")
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository	userRepository;

    @Override
    public	UserDetails	loadUserByUsername(String	email)	throws UsernameNotFoundException {
        Optional<User> optUser = userRepository.getByEmail(email);
        if (optUser.isPresent()) {
            userRepository.updateUserLogin(optUser.get());
            return optUser.get();
        } else {
            throw new UsernameNotFoundException("Usuário	não	encontrado");
        }
       /* Optional<br.com.siecola.gae.poc1.model.User>optUser	= userRepository.getByEmail(email);
        if	(optUser.isPresent())	{
            return	optUser.get();
        }	else	{
            throw new	UsernameNotFoundException("Usuário	não	encontrado")
                    ;
        }
    }*/
    }
}
